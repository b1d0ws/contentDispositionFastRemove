# Content Disposition Fast Remove

## Description

This extension streamlines testing for image uploads by automatically clearing all content or only image-based content within multipart form-data requests. This is especially beneficial when dealing with large image files, eliminating the need for manually selecting and deleting the content.

Additionally, the extension offers two options: replacing the image content with either the EICAR test file or a PHP webshell payload. These options facilitate common security tests involving file upload vulnerabilities, saving time and enhancing efficiency during assessments.

## Features

- Clear All Content Disposition
- Clear Only Images Content Disposition
- Replace Image with EICAR file content
- Replace Image with PHP Reverse Shell content

## Usage

1. In Burp Suite navigate to the Extensions tab.
2. Select "Add".
3. Leave Extension Type as "Java" and choose "Select file…".
4. Navigate to the included "ContentDispositionRemoveExtension.jar" file or your JAR compiled
from source, then click "Open".
5. Click "Next" to load the plugin.
6. Go to a request on Proxy or Repeater and use the extension with the right click
