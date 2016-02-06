Ilves
=====

Ilves is a lean Java web site development framework based on Vaadin framework. Ilves provides responsive user
interface and advanced security functions in addition to host of  other useful features. It is perfect for crafting
user interfaces for low budget company tools and DIY projects without sacrificing user experience.

Demo is available at Heroku:

https://ilves.herokuapp.com/

Features and other details can be found from Ilves Wiki:

https://github.com/bubblecloud/ilves/wiki

Seed project is available for cloning in GitHub:

https://github.com/bubblecloud/ilves-seed

5 minute tutorial at Ilves Wiki:

https://github.com/bubblecloud/ilves/wiki/5-Minute-Tutorial


Usage
-----

Vaadin custom components can be used as is:

```
/**
 * Custom component example.
 *
 * @author Tommi S.E. Laukkanen
 */
public class HelloComponent extends CustomComponent {

    /**
     * Default constructor which sets up the component.
     */
    public HelloComponent() {
        final User user = Ilves.getCurrentUser();
        final String greeting;
        if (user == null) {
            greeting = "Hello";
        } else {
            greeting = "Hi, " + user.getFirstName() + "!";
        }
        final Label label = new Label(greeting);
        label.setStyleName("custom-welcome-label");
        setCompositionRoot(label);
    }

}
```

Embedding Ilves to console applications:

```
// Construct jetty server.
final Server server = Ilves.configure(PROPERTIES_FILE_PREFIX, LOCALIZATION_BUNDLE_PREFIX, PERSISTENCE_UNIT);

// Initialize modules
Ilves.initializeModule(AuditModule.class);

Ilves.addNavigationCategoryPage(0, "custom");
Ilves.addChildPage("custom", "comments", DefaultValoView.class);
Ilves.setPageComponent("comments", Slot.CONTENT, HelloComponent.class);
Ilves.setPageComponent("comments", Slot.FOOTER, CommentingComponent.class);
Ilves.setDefaultPage("comments");

// Start server.
server.start();

// Wait for exit of the Jetty server.
server.join();
```

