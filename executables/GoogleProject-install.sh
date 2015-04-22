#!/bin/bash

sudo yum -y install tomcat6 java-1.8.0-openjdk-devel apache-maven
sudo cp OAuth2v1/conf-files/settings.xml /opt/maven/conf/settings.xml
sudo cp OAuth2v1/conf-files/tomcat-users.xml /usr/share/tomcat6/conf/tomcat-users.xml
sudo touch /etc/profile.d/maven.sh
sudo bash -c "echo $'export MAVEN_HOME=/opt/maven\nexport MAVEN=\$MAVEN_HOME/bin\nexport PATH=\$MAVEN:\$PATH' > /etc/profile.d/maven.sh"

for i in /etc/profile.d/*.sh ; do
    if [ -r "$i" ]; then
        . $i
    fi
done

(cd /heroes/u1/rm934/OAuth2v1 && mvn clean compile)
