Requirements
--------
* Java 11+
* Java SDK 16
* Javafx SDK 16


Setup IntelliJ IDEA
--------
1. Set Java SDK version in File -> Project Structure -> Project
![add java sdk](docs/assets/1.png)

2. Link project third party libraries in File -> Project Structure -> Modules -> Dependencies
![add third party libs](docs/assets/2.png)

Then select `lib` folder in the root of the project.

3. Configure server runner
![server config](docs/assets/3.png)

4. Configure clients
VM line:
```
--module-path /YOUR/PATH/TO/JavaFX-lib/javafx-sdk-16/lib --add-modules javafx.controls,javafx.fxml
```
![client config](docs/assets/4.png)
