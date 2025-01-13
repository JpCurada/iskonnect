# ISKOnnect
Team 1 - ALPHAB1T Project for COMP 20083

## Project Description
ISKOnnect is a desktop application designed to help PUP students share and access study materials in one centralized platform. Our goal is to create a digital library where students can help each other excel in their studies through shared resources.

## Development Team (ALPHAB1T)
- CURADA, John Paul M.
- FAELDONIA, Elias Von Isaac R.
- GENTE, Daisyrie I.
- LUCERO, Ken Audie S.
- OJA, Ma. Izabelle L.
- RACELIS, Michael Richmond V.
- ZARAGOZA, Marie Criz

## Project Setup Instructions

### 1. **Verify Java Installation** 
- Java SDK 23 ✓
- Java version 23 ✓

### 2. **Set Environment Variables**
1. Open Windows System Properties (Right-click 'This PC' → Properties → Advanced System Settings)
2. Click "Environment Variables"
3. Under "System Variables", set:
   ```
   JAVA_HOME = C:\Program Files\Java\jdk-23
   ```
4. Edit "Path" variable and add:
   ```
   %JAVA_HOME%\bin
   ```

### 3. **Install VS Code**
- Download from: https://code.visualstudio.com/
- Install VS Code

### 4. **Install VS Code Extensions**
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Install:
   - "Extension Pack for Java"
   - "Maven for Java"

### 5. **Install Git**
1. Download from: https://git-scm.com/downloads
2. During installation, choose:
   - "Git from the command line and also from 3rd-party software"
   - "Use Visual Studio Code as Git's default editor"

### 6. **Install Maven**
1. Download from: https://maven.apache.org/download.cgi (get the `apache-maven-3.9.9-bin.zip`)
2. Extract to `C:\Program Files\Apache\apache-maven-3.9.9`
3. Add to Environment Variables:
   ```
   M2_HOME = C:\Program Files\Apache\apache-maven-3.9.9
   ```
4. Add to Path:
   ```
   %M2_HOME%\bin
   ```

### 7. **Verify Installations**
Open Command Prompt and check:
```cmd
java --version
javac --version
git --version
mvn --version
```

### 8. **Project Setup**
1. Clone the repository:
   ```cmd
   git clone https://github.com/JpCurada/iskonnect.git
   cd iskonnect
   ```
2. Run with Maven:
   ```cmd
   mvn run
   ```

## Project Structure
```
iskonnect/
├── LICENSE
├── README.md
├── config/
├── lib/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── iskonnect/
│       │           ├── controllers/
│       │           ├── models/
│       │           ├── services/
│       │           ├── utils/
│       │           └── views/
│       └── resources/
│           ├── css/
│           ├── fxml/
│           └── images/
```

## Features
- Student authentication and registration
- Upload and download study materials
- Search and filter resources
- Rating and commenting system
- Achievement badges
- Admin moderation tools

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- PUP College of Computer and Information Sciences
- COMP 20083 Course
- Master Chris Piamonte