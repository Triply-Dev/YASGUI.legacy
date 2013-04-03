This code gets executed (via github service notification) when code is pushed to master or dev 

Use:

* Make sure user apache (www-data) has read/write access to git directories
* Preferably, have a cleanup crontab script deleting trash files from e.g. /var/lib/tomcat7/.trash (path may differ, is set in hookConfig.ini)
