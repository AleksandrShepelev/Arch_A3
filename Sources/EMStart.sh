#javac *.java
rmiregistry &
java MessageManager

#lsof -i :1099