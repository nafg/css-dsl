# CSS DSL Generator

CSS DSL is a Scala code generator that creates type-safe DSL wrappers for CSS frameworks (Bootstrap, Bulma, Semantic UI, Font Awesome, etc.). It downloads CSS files from CDNs, parses them to extract class names, and generates Scala code for both JVM (scalatags) and Scala.js (scalajs-react) targets.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Environment Configuration

This repository includes a GitHub Copilot agent environment configuration (`.github/copilot/environment.yml`) that mirrors the CI workflow setup. The environment configuration ensures consistent development conditions with:

- Ubuntu latest OS matching CI
- Java 11 (temurin distribution) with SBT caching
- SBT 1.11.3 setup identical to CI workflow
- Verified CDN access to cdn.jsdelivr.net and stackpath.bootstrapcdn.com  
- Network validation commands for CSS framework downloads
- Support for Scala 2.13.x and 3.3.x cross-compilation

The environment setup replicates the exact CI conditions to ensure reliable code generation and testing.

## Working Effectively

### Prerequisites and Setup
- Install Java 11+ (project tested with Java 11, works with Java 17+)
- Install SBT 1.11.3:
  Follow the official SBT installation instructions for your platform:  
  https://www.scala-sbt.org/download.html

  For Linux/macOS, you can use the installer script which verifies the download:
  ```bash
  curl -s https://raw.githubusercontent.com/sbt/sbt/develop/bin/install-sbt | bash
  export PATH="$HOME/bin:$PATH"
  sbt --version

### Network Requirements
- **CRITICAL**: This project requires internet access to download CSS files from CDNs during build
- Build downloads CSS from: cdn.jsdelivr.net, stackpath.bootstrapcdn.com
- **IMPORTANT**: CDN URLs are allowlisted in GitHub Copilot environments for this repository
- Build fails in environments without external network access with `java.net.UnknownHostException`
- If network access is unavailable, code generation cannot proceed

### Building the Project
- **NETWORK REQUIRED**: All build commands require internet access to CDNs
- **ALLOWLISTED**: CDN access is configured for GitHub Copilot environments in this repository
- Basic compilation:
  ```bash
  sbt compile
  ```
- **TIMING**: Compilation takes 15-30 seconds when network is available
- **TIMING**: Initial SBT startup takes 10-15 seconds for dependency resolution
- Build failure example when network unavailable:
  ```
  [error] java.net.UnknownHostException: cdn.jsdelivr.net
  ```

### Testing
- Run tests (compilation tests):
  ```bash
  sbt test
  ```
- **TIMING**: Test suite takes 30-60 seconds. NEVER CANCEL. Set timeout to 90+ seconds.
- **CRITICAL**: Tests are primarily compilation tests - if generated code compiles, tests pass

### Project Structure
- **No src/ directories**: This is a generator project that creates subprojects dynamically
- Generated subprojects (visible after successful build):
  - `bootstrap3_scalatags`, `bootstrap3_scalajsreact`
  - `bootstrap4_scalatags`, `bootstrap4_scalajsreact`  
  - `bootstrap5_scalatags`, `bootstrap5_scalajsreact`
  - `bulma_scalatags`, `bulma_scalajsreact`
  - `semanticui_scalatags`, `semanticui_scalajsreact`
  - `fomanticui_scalatags`, `fomanticui_scalajsreact`
  - `fontawesome_scalatags`, `fontawesome_scalajsreact`

### Working with Subprojects
- List all projects:
  ```bash
  sbt projects
  ```
- Switch to specific project:
  ```bash
  sbt 'project bootstrap4_scalatags'
  ```
- Check project dependencies:
  ```bash
  sbt 'project bootstrap4_scalatags' 'show libraryDependencies'
  ```

### Cross-Compilation
- Supported Scala versions: 2.13.16, 3.3.6 (default)
- Cross-compile for all Scala versions:
  ```bash
  sbt +compile
  sbt +test
  ```

## Limitations and Workarounds

### Network Access Limitation
- **NETWORK DEPENDENCY**: Project requires external internet access to download CSS files from CDNs
- **ALLOWLISTED**: CDN URLs (cdn.jsdelivr.net, stackpath.bootstrapcdn.com) are allowlisted for GitHub Copilot environments
- **NO OFFLINE MODE**: Project has no offline fallback - CSS files must be downloaded fresh
- Code generation happens at compile time, not runtime

### Generated Code Location
- Generated sources are in: `[project]/target/scala-[version]/src_managed/main/cssdsl/[framework]/`
- Target directories are created only after successful build
- Clean removes all generated code: `sbt clean`

## Validation
- **NETWORK CONNECTIVITY**: Verify network access to CDNs for builds (allowlisted in GitHub Copilot environments)
- Test basic SBT functionality first: `sbt projects`
- **Build validation requires network access** - cannot validate build offline
- Generated DSL provides type-safe CSS class names as Scala methods
- Example validation:
  ```bash
  sbt 'project bootstrap4_scalatags' compile
  ```

## Common Tasks

### Development Workflow (with network access)
1. Clean previous build: `sbt clean`
2. Generate and compile all frameworks: `sbt compile`
3. Test compilation: `sbt test`
4. Work with specific framework: `sbt 'project bootstrap4_scalatags'`

### Updating Framework Versions
- CSS framework versions are specified in build.sbt
- Latest versions are fetched from npm automatically
- Regeneration requires: `sbt clean compile`

### Custom Tasks
- Update README.md tables: `sbt generateInstallInstructions`
- Check GitHub workflow: `sbt githubWorkflowCheck`

### Troubleshooting
  rm -f ~/.sbt/boot/sbt.boot.lock
  ```
- **SBT startup time**: Initial SBT commands can take 10-15 seconds for dependency resolution
- **Network timeouts**: If CSS downloads timeout, retry the build command

## Repository Structure
```
.
├── README.md                          # Main documentation
├── build.sbt                          # Main build configuration
├── ci.sbt                             # CI/CD configuration  
├── generateInstructions.sbt           # README generation task
├── project/
│   ├── build.properties              # SBT version (1.11.3)
│   ├── plugins.sbt                   # SBT plugins
│   ├── GeneratorPlugin.scala         # Code generation plugin
│   ├── Generator.scala               # DSL code generator
│   ├── TargetImpl.scala              # Target library implementations
│   └── CssExtractor.scala            # CSS parsing logic
└── .github/workflows/ci.yml          # GitHub Actions CI
```

## Framework Support
| Framework    | Versions | JVM (scalatags) | Scala.js (scalajs-react) |
|--------------|----------|-----------------|---------------------------|
| Bootstrap 3  | 3.4.1    | ✓               | ✓                         |
| Bootstrap 4  | 4.6.2    | ✓               | ✓                         |
| Bootstrap 5  | 5.3.8    | ✓               | ✓                         |
| Bulma        | 1.0.4    | ✓               | ✓                         |
| Semantic UI  | 2.5.0    | ✓               | ✓                         |
| Fomantic UI  | 2.9.0    | ✓               | ✓                         |
| Font Awesome | 7.0.0    | ✓               | ✓                         |

## Dependencies
- Scala 3.3.6 (primary), 2.13.16 (cross-compile)
- SBT 1.11.3
- scalatags 0.13.1 (JVM target)
- scalajs-react 2.1.2 (Scala.js target)
- ph-css (CSS parsing)
- Scalameta (code generation)