import re

def update_app(content):
    # Remove MCP initialization from ProdyApplication.kt
    content = re.sub(r'// Launch MCP Diagnostic Server in debug builds.*?applicationScope\.launch\(Dispatchers\.IO\) \{.*?\}', '', content, flags=re.DOTALL)
    return content

if __name__ == "__main__":
    filepath = 'app/src/main/java/com/prody/prashant/ProdyApplication.kt'
    with open(filepath, 'r') as f:
        content = f.read()

    updated_content = update_app(content)

    with open(filepath, 'w') as f:
        f.write(updated_content)
