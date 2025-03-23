package org.example.endtoend;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/org/example/endtoend/features",
        glue = "org/example/endtoend/steps",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class EndToEndTestRunner {
}