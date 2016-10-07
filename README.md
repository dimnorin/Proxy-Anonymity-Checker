# Proxy-Anonymity-Checker
Check your proxies anonymity and divide them into high and low anonimus. 

Proxy anonimity is determined by getting HTTP headers through this proxy. 
If headers is a one of following:

  - X-Forwarded-For
  - X-Real-IP
  - Via
   
then proxy is low anonimus, otherwise high.

Once you've downloaded Proxy Anonymity Checker, you can start the checker with the following steps:

  - Put your proxies list into file **proxy_src.txt** in app root folder.
  - Put **headers.php** file on your server.
  - Write all config into **app.properties** file.
  - Run the checker:
```
java -jar checker.jar
```
Your HIGH Anonimous proxies will be in **proxy_dst_high.txt** and LOW Anonimous will be in **proxy_dst_low.txt**
