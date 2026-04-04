import re

def revert_libs(content):
    # Revert Kotlin and KSP to previous versions to avoid Hilt incompatibility
    content = re.sub(r'kotlin = "2.1.0"', 'kotlin = "2.0.21"', content)
    content = re.sub(r'ksp = "2.1.0-1.0.29"', 'ksp = "2.0.21-1.0.27"', content)
    return content

if __name__ == "__main__":
    filepath = 'gradle/libs.versions.toml'
    with open(filepath, 'r') as f:
        content = f.read()

    reverted_content = revert_libs(content)

    with open(filepath, 'w') as f:
        f.write(reverted_content)
