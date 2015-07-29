/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.ExistanceDependency;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.OrderDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.ReleaseDependency;
import java.util.Arrays;

/**
 *
 * @author Raido
 */
public final class DependencyGraph {

    private final DependencyGraph.Dependency[] dependencies;

    public DependencyGraph(Project project) {
        Dependencies deps = project.getDependencies();
        Features feats = project.getFeatures();
        
        OrderDependency[] odeps = deps.getTypedDependencies(OrderDependency.class, null);
        ExistanceDependency[] edeps = deps.getTypedDependencies(ExistanceDependency.class, null);
        ReleaseDependency[] rdeps = deps.getTypedDependencies(ReleaseDependency.class, null);

        int index = 0;
        int count = odeps.length + edeps.length + rdeps.length;
        this.dependencies = new DependencyGraph.Dependency[count];

        for (OrderDependency d : odeps) {
            int i1 = feats.getFeatureIndex(d.getPrimary());
            int i2 = feats.getFeatureIndex(d.getSecondary());
            String str = OrderDependency.getToken(d, project);
            DependencyGraph.Dependency dep = new DependencyGraph.Dependency(str, i1, i2);

            this.dependencies[index] = dep;
            index++;
        }

        for (ExistanceDependency d : edeps) {
            int i1 = feats.getFeatureIndex(d.getPrimary());
            int i2 = feats.getFeatureIndex(d.getSecondary());
            String str = ExistanceDependency.getToken(d);
            DependencyGraph.Dependency dep = new DependencyGraph.Dependency(str, i1, i2);

            this.dependencies[index] = dep;
            index++;
        }

        for (ReleaseDependency d : rdeps) {
            int fi = feats.getFeatureIndex(d.getFeature());
            String str = ReleaseDependency.getToken(d, project);
            DependencyGraph.Dependency dep = new DependencyGraph.Dependency(str, fi, fi);

            this.dependencies[index] = dep;
            index++;
        }

        if (this.dependencies.length > 0) {
            this.processHeigths(feats.getFeatureCount());
            this.sortAttachmentPoints(feats.getFeatureCount());
        }
    }

    private void processHeigths(int features) {
        int[] heights = new int[features << 1];

        // Distance == 1
        for (DependencyGraph.Dependency d : this.dependencies) {
            if (d.distance == 1) {
                int n1 = (d.node1 << 1) + 1;
                int n2 = d.node2 << 1;
                int h1 = heights[n1];
                int h2 = heights[n2];
                
                d.height = (h1 > h2 ? h1 : h2) + 1;
                heights[n1] = d.height;
                heights[n2] = d.height;
            }
        }

        // Distance > 1
        for (int dst = 2; dst < features; dst++) {
            for (DependencyGraph.Dependency d : this.dependencies) {
                if (d.distance == dst) {
                    int maxh = 0;
                    
                    { // First node
                        int n = heights[(d.node1 << 1) + 1];
                        maxh = (n > maxh) ? n : maxh;
                    }
                    
                    for (int n = d.node1 + 1; n < d.node2; n++) { // Middle nodes
                        int h1 = heights[(n << 1) + 0];
                        int h2 = heights[(n << 1) + 1];
                        int max2 = (h1 > h2) ? h1 : h2;
                        
                        maxh = (max2 > maxh) ? max2 : maxh;
                    }
                    
                    { // Last node
                        int n = heights[d.node2 << 1];
                        maxh = (n > maxh) ? n : maxh;
                    }

                    d.height = maxh + 1;
                    heights[(d.node1 << 1) + 1] = d.height;
                    for (int n = d.node1 + 1; n < d.node2; n++) {
                        heights[(n << 1) + 0] = d.height;
                        heights[(n << 1) + 1] = d.height;
                    }
                    heights[d.node2 << 1] = d.height;
                }
            }
        }

        // Distance < 1
        for (DependencyGraph.Dependency d : this.dependencies) {
            if (d.distance < 1) {
                int n1 = d.node1 << 1;
                int n2 = (d.node1 << 1) + 1;
                int h1 = heights[n1];
                int h2 = heights[n2];
                
                d.height = (h1 > h2 ? h1 : h2) + 1;
                heights[n1] = d.height;
                heights[n2] = d.height;
            }
        }
    }

    private void sortAttachmentPoints(int features) {
        DependencyGraph.Connection[] conns = new DependencyGraph.Connection[this.dependencies.length];
        int used;

        for (int fi = 0; fi < features; fi++) {
            used = 0;

            for (DependencyGraph.Dependency d : this.dependencies) {
                DependencyGraph.Connection conn;

                if (d.node1 == fi || d.node2 == fi) {
                    if (conns[used] != null) {
                        conn = conns[used];
                    } else {
                        conn = new DependencyGraph.Connection();
                        conns[used] = conn;
                    }
                    used++;

                    conn.vector = (d.node2 - d.node1) * (d.node1 == fi ? 1 : -1);
                    conn.height = d.height;
                    conn.dependency = d;
                }
            }

            if (used > 0) {
                Arrays.sort(conns, 0, used);
                for (int di = 0; di < used; di++) {
                    DependencyGraph.Dependency dep = conns[di].dependency;
                    float value = 0.2f + ((di + 0.5f) / used) * 0.6f;
                    if (dep.node1 == fi) {
                        dep.anchor1 = value;
                    } else {
                        dep.anchor2 = value;
                    }
                }
            }
        }
    }

    public DependencyGraph.Dependency getDependency(int index) {
        return this.dependencies[index];
    }

    public int getDependencyCount() {
        return this.dependencies.length;
    }

    public final class Dependency {

        public final String text;
        public final int node1, node2, distance;
        private float anchor1, anchor2;
        private int height;

        private Dependency(String txt, int n1, int n2) {
            this.text = (txt != null) ? txt : new String();
            if (n1 < 0 || n2 < 0) {
                throw new ArrayIndexOutOfBoundsException("Invalid node index");
            }

            this.node1 = (n1 < n2) ? n1 : n2;
            this.node2 = (n1 > n2) ? n1 : n2;
            this.distance = this.node2 - this.node1;

            this.anchor1 = 0.5f;
            this.anchor2 = 0.5f;
            this.height = 1;
        }

        public float getAnchor1() {
            return this.anchor1;
        }

        public float getAnchor2() {
            return this.anchor2;
        }

        public int getHeight() {
            return this.height;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    private final class Connection implements Comparable<Connection> {

        private DependencyGraph.Dependency dependency;
        private int vector, height;

        @Override
        public int compareTo(Connection other) {
            int mul = this.vector * other.vector;
            
            if (mul == 0) {
                return (this.vector - other.vector);
            }else if (mul > 0) {
                return (other.height - this.height) * this.vector;
            }
            
            return this.vector;
        }
    }
}
