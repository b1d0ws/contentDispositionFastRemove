import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

/**
 * BurpExtender class to register
 * the extension with Burp Suite
 */
public class BurpExtender implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Content-Disposition Fast Remove");
        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));
        api.logging().logToOutput("Content-Disposition Fast Remove extension loaded!\r\nhttps://github.com/b1d0ws/");
    }
}