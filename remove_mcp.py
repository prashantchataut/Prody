import re

def remove_mcp(content):
    # Remove MCP related libraries and versions to fix Kotlin metadata incompatibility
    content = re.sub(r'mcp = "0.9.0"', '', content)
    content = re.sub(r'ktor = "3.0.1"', '', content)
    content = re.sub(r'mcp-kotlin-sdk = \{ group = "io.modelcontextprotocol", name = "kotlin-sdk", version.ref = "mcp" \}', '', content)
    content = re.sub(r'ktor-server-core = \{ group = "io.ktor", name = "ktor-server-core", version.ref = "ktor" \}', '', content)
    content = re.sub(r'ktor-server-cio = \{ group = "io.ktor", name = "ktor-server-cio", version.ref = "ktor" \}', '', content)
    content = re.sub(r'ktor-server-sse = \{ group = "io.ktor", name = "ktor-server-sse", version.ref = "ktor" \}', '', content)
    return content

if __name__ == "__main__":
    filepath = 'gradle/libs.versions.toml'
    with open(filepath, 'r') as f:
        content = f.read()

    updated_content = remove_mcp(content)

    with open(filepath, 'w') as f:
        f.write(updated_content)
