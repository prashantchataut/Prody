import re

def update_build(content):
    # Remove MCP and Ktor dependencies from build.gradle.kts
    content = re.sub(r'// MCP Diagnostic Server \(Debug only\).*?debugImplementation\(libs\.ktor\.server\.sse\)', '', content, flags=re.DOTALL)
    return content

if __name__ == "__main__":
    filepath = 'app/build.gradle.kts'
    with open(filepath, 'r') as f:
        content = f.read()

    updated_content = update_build(content)

    with open(filepath, 'w') as f:
        f.write(updated_content)
