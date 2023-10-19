package cn.mercury.xcode.mybatis.settings;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SqlParameterStorage {

    List<ParameterGroup> parameterGroups;

    public void add(ParameterGroup group) {
        parameterGroups.add(group);
        group.setTimestamp(new Date());
    }

    public List<ParameterGroup> findGroups(String namespace, String id) {
        return parameterGroups.stream().filter(g ->
                        g.getNamespace().equals(namespace) && g.getId().equals(id)
                )
                .sorted((a, b) -> b.timestamp.compareTo(a.timestamp))
                .collect(Collectors.toList());
    }

    public static class ParameterGroup {
        private String name;

        private String namespace;

        private String id;

        private Date timestamp;

        HashMap<String, String> params = new HashMap<>();

        public String getName() {
            return this.name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public HashMap<String, String> getParams() {
            return this.params;
        }

        public void setParams(HashMap<String, String> value) {
            this.params = value;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }

}
