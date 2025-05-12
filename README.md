**HOW TO DEPLOY APPLICATION**

To deploy the application you need to pack custom-springboot-starter from the module common with the command "_mvn clean install_", 
then pack the rest of the modules with the command "_mvn clean install -DskipTests -pl !common_".

If you need to deploy the application to **Docker** the command "_docker-compose up -d_" will be enough.
Otherwise if you need **minikube** then you should upload project images to DockerHub, then run the following commands:
1. "_minikube start_"
2. "_kubectl apply -f ./k8s/***.yaml_"
3. After apply all .yaml, you need to run command "_minikube tunnel_"
