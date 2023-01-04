
###How To Use
- mvn clean compile install <br />
- docker build . -f Dockerfile -t sdd_zare_interview <br />
- docker run -it -p 8080:8080 sdd_zare_interview <br />

swagger will be available at port 8080 <br />
http://127.0.0.1:8080/swagger-ui/ <br />

### EndPoint Test
- install http (if you haven't installed already)
>echo -n 'admin|admin' | base64 <br />
- returns 'YWRtaW58YWRtaW4='
>echo -n 'user1|user1' | base64 <br />
- returns 'dXNlcjF8dXNlcjE='

### create account for user1 and user2  
- echo '{"accountHolder":"user1","accountType":"SAVING","balance":400}' | http POST :8080/account Authorization:'YWRtaW58YWRtaW4='  <br />
- echo '{"accountHolder":"user2","accountType":"SAVING","balance":50}' | http POST :8080/account Authorization:'YWRtaW58YWRtaW4='

### transfer money
- echo '{"transferorAccountNumber":"0001","transfereeAccountNumber":"0002","amount":31 }' | http POST :8080/transaction Authorization:'dXNlcjF8dXNlcjE='
- http :8080/transaction/0001 Authorization:'dXNlcjF8dXNlcjE='
