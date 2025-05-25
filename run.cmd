 mvn clean dependency:copy-dependencies -DoutputDirectory=libs compile

java -Dfile.encoding=UTF-8 -classpath ".\target\classes:.\libs\*" top.kloping.CliMain
