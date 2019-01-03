package com.example.twentyfive;


import org.springframework.stereotype.Service;


import java.util.Arrays;

@Service
public class DdlCreateMapping {
    public static String createMappings(String ddl, String parent, int flag) throws Exception {
        String[] types1 = new String[]{"tinyint", "smallint", "mediumint", "boolean"};//integer
        String[] types2 = new String[]{"int", "bigint", "float", "double", " real", "bit", "serial"};//long
        String[] types3 = new String[]{"date", "datetime", "timestamp", "time", "year", "char", "varchar", "tinytext"};//text
        String[] strings = ddl.split(",");
        StringBuffer mappings = null;
        for (int i = 0; i < strings.length; i++) {
            String[] split = strings[i].split("`");
            String ignore_above = "256";

            if (i == 0) {
                mappings = init(split[1]);
                if (parent != null) {
                    mappings.append("\"_parent\": {\n" +
                            "          \"type\": \"" + parent + "\"\n" +
                            "        },\n" +
                            "        \"_routing\": {\n" +
                            "          \"required\": true\n" +
                            "        },\n" +
                            "        ");
                }
                mappings.append("\n" +
                        "        \"properties\": {");
                mappings.append("\"id\": {\n" +
                        "        \"type\": \"long\"\n" +
                        "      }");
            }

            if (i != 0) {

                if (!strings[i].contains("`")) {
                    continue;
                }
                if (strings[i].contains("PRIMARY KEY")) {
                    mappings.append("\n" +
                            "  }\n" +
                            "}");
                    System.out.println(mappings);
                    return mappings.toString();
                }
                if (split[1].contains("def") && flag == 1) {
                    mappings.append("\n" +
                            "        \""+split[1]+"\": {\n" +
                            "          \"type\": \"text\",\n" +
                            "          \"fields\": {\n" +
                            "            \"keyword\": {\n" +
                            "              \"type\": \"keyword\",\n" +
                            "              \"ignore_above\": 256\n" +
                            "            }\n" +
                            "          }\n" +
                            "        }");
                    continue;
                }
                mappings.append(",\n" +
                        "      \"" + split[1] + "\": {\n" +
                        "        \"type\": ");
                String s = split[2].split("\\(")[0].substring(1);
                if (s.equals("varchar")) {
                    String s1 = split[2].split("\\(")[1].split("\\)")[0];
                    if (Integer.valueOf(s1) > 256)
                        ignore_above = s1;
                }

                if (Arrays.asList(types1).contains(s)) {
                    mappings.append("\"integer\"\n" +
                            "        }");
                } else if (Arrays.asList(types2).contains(s)) {
                    mappings.append("\"long\"\n" +
                            "        }");
                } else if (s.contains("datetime")) {
                    mappings.append(" \"date\",\n" +
                            "            \"ignore_malformed\": true,\n" +
                            "            \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd||epoch_millis\"\n" +
                            "          }");

                } else if (s.equals("decimal")) {
                    mappings.append("\"scaled_float\",\n" +
                            "            \"scaling_factor\": 100\n" +
                            "          }");
                } else {
                    mappings.append("\"text\",\n" +
                            "            \"fields\": {\n" +
                            "              \"keyword\": {\n" +
                            "                \"type\": \"keyword\",\n" +
                            "                \"ignore_above\": " + ignore_above + "\n" +
                            "              }\n" +
                            "            }\n" +
                            "          }");
                }
            }

        }
        return null;
    }

    static StringBuffer init(String dbName) {
        StringBuffer mappings = new StringBuffer("\"" + dbName + "\": {\n" +
                "    \"dynamic\": \"true\",\n" +
                "    \"_all\": {\n" +
                "      \"enabled\": false\n" +
                "      },");
        return mappings;
    }
}
