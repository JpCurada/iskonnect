# ISKOnnect Development Guide

## Git & GitHub Setup
1. **Clone Repository**
```bash
git clone https://github.com/your-username/iskonnect.git
cd iskonnect
```

2. **Branch Naming Convention**
```
feature/[what-youre-working-on]
Examples:
- feature/login-screen
- feature/database-connection
- feature/user-authentication
```

3. **Commit Message Format**
```
type: description

Types:
- feat: New feature
- fix: Bug fix
- style: UI/styling changes
- docs: Documentation
- refactor: Code restructuring
- test: Adding tests

Example:
"feat: add login screen layout"
"fix: correct database connection string"
```

## Running Project Locally
1. **Prerequisites**
- Java JDK 17 or higher
- Maven
- VS Code with Java extensions
- Git

2. **Environment Setup**
```bash
# Check installations
java --version
mvn --version
git --version
```

3. **Running the App**
```bash
# Build project
mvn clean install

# Run application
mvn javafx:run
```

## Development Workflow
1. **Starting New Work**
```bash
# Get latest changes
git checkout main
git pull origin main

# Create new branch
git checkout -b feature/your-feature
```

2. **Making Changes**
```bash
# Save changes
git add .
git commit -m "feat: what you did"

# Push changes
git push origin feature/your-feature
```

3. **Submitting Work**
- Create Pull Request on GitHub
- Wait for review
- Address feedback if any

## File Naming Conventions
```
Java Files:
- Controllers: *Controller.java (LoginController.java)
- Models: Singular form (User.java, Material.java)
- Utils: *Utils.java or *Service.java

FXML Files:
- Screen names: lowercase_with_underscore.fxml
- Located in: src/main/resources/fxml/

CSS Files:
- Feature specific: feature_name.css
- Located in: src/main/resources/styles/
```

## Team Assignments

### UI Team (Marie, Iza)
- Work in `src/main/resources/fxml/`
- Create branches like `feature/ui-screen-name`
- Follow FXML naming convention

### Database Team (Von, JP, Ken)
- Work in `src/main/java/com/iskonnect/`
- Create branches like `feature/db-feature-name`
- Focus on services and utils packages

### Best Practices
1. Always pull before starting work
2. Test locally before pushing
3. Keep commits small and focused
4. Comment your code
5. Update documentation when needed

## Common Issues & Solutions
1. **Merge Conflicts**
```bash
git checkout main
git pull origin main
git checkout your-branch
git merge main
# Resolve conflicts in VS Code
```

2. **Build Errors**
- Check dependencies in pom.xml
- Verify Java version
- Clean and rebuild project

3. **Database Connection**
- Use environment variables for credentials
- Test connections locally first
- Document any configuration changes
