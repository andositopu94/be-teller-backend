nohup java7/bin/java -Xms1024m -Xmx4096m -XX:PermSize=512m -jar -Dspring.config.location=./application-sumut-xyz.properties teller-backend-0.0.1-SNAPSHOT.jar &

nohup java7/bin/java -jar -Dspring.config.location=./application.properties -Xms1024m -Xmx2048m teller-backend-0.0.1-SNAPSHOT.jar &

nohup java -jar -Dspring.config.location=./application.properties -Xms1024m -Xmx4096m teller-backend-0.0.1-SNAPSHOT.jar &

nohup java -jar -Dspring.config.location=./properties/application.properties teller-backend-0.0.1-SNAPSHOT.jar -Xms1024m -Xmx2048m &