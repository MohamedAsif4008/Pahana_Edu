# Engineering Management System
**Student Name:** Mohamed Asif  
**Module:** CIS6003 - Advanced Programming  
**Assessment:** WRIT1 - May 2025

## Project Overview
This project demonstrates a distributed web application implementing engineering management principles through a multi-tiered architecture. The system showcases proper design patterns, UML modeling, and modern web development practices using Java technologies.

## 🎯 Learning Objectives Addressed
- **LO I:** UML system design with comprehensive diagrams and design decisions
- **LO II:** Implementation of design patterns, distributed architecture, and database integration
- **LO III:** Professional version control practices and documentation standards

## 🏗️ System Architecture
The application follows a **3-tier architecture**:
- **Presentation Layer:** JSP views with responsive UI components
- **Business Logic Layer:** Java servlets implementing design patterns
- **Data Access Layer:** MySQL database with proper normalization

### Design Patterns Implemented
- **MVC (Model-View-Controller):** Clear separation of concerns
- **DAO (Data Access Object):** Database abstraction layer
- **Singleton:** Database connection management
- **Factory:** Object creation patterns

## 🛠️ Technology Stack
- **Backend:** Java Servlets, JSP
- **Database:** MySQL 8.0
- **Server:** Apache Tomcat 9.0
- **Frontend:** HTML5, CSS3, Bootstrap 4
- **Build Tool:** Maven
- **Version Control:** Git/GitHub

## 📋 Features
### Core Functionality
- User authentication and session management
- Role-based access control (Admin/User)
- CRUD operations for system entities
- Input validation and error handling
- Report generation capabilities

### Advanced Features
- RESTful web services
- Database transactions
- Session/cookie management
- Responsive web design
- Search and filtering capabilities

## 🗂️ Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── controllers/     # Servlet controllers
│   │   ├── models/         # Data models and entities
│   │   ├── dao/           # Data access objects
│   │   ├── utils/         # Utility classes
│   │   └── services/      # Business logic services
│   ├── webapp/
│   │   ├── WEB-INF/       # Configuration files
│   │   ├── views/         # JSP view files
│   │   ├── css/          # Stylesheets
│   │   ├── js/           # JavaScript files
│   │   └── images/       # Static resources
│   └── resources/
│       └── schema.sql     # Database schema
├── test/                  # Test classes
└── docs/                 # UML diagrams and documentation
```

## 🎨 UML Design Documentation
The system design includes comprehensive UML diagrams:

### Use Case Diagram
- **Actors:** Admin, User, Guest
- **Use Cases:** Login, Manage Items, Generate Reports, View Dashboard
- **Relationships:** Proper use of <<include>> and <<extend>> stereotypes

### Class Diagram
- **Classes:** User, Item, Order, Admin with proper attributes and methods
- **Relationships:** Association, aggregation, and composition
- **Access Modifiers:** Clear public/private visibility
- **Multiplicity:** Proper cardinality relationships

### Sequence Diagrams
- **User Login Process:** Authentication flow
- **Item Management:** CRUD operations sequence
- **Report Generation:** Multi-layer interaction

## 🧪 Testing Strategy
### Test-Driven Development Approach
- **Unit Tests:** Individual component testing
- **Integration Tests:** Multi-layer functionality verification
- **System Tests:** End-to-end workflow validation

### Test Coverage
- Input validation testing
- Database operation testing
- User authentication testing
- Business logic verification

### Test Automation
- JUnit framework for automated testing
- Mock objects for isolated testing
- Continuous integration practices

## 🚀 Installation & Setup
### Prerequisites
- Java JDK 11 or higher
- Apache Tomcat 9.0
- MySQL 8.0
- Maven 3.6+

### Database Setup
1. Create MySQL database: `engineering_mgmt`
2. Run schema script: `src/main/resources/schema.sql`
3. Update database configuration in `web.xml`

### Application Deployment
1. Clone repository: `git clone [repository-url]`
2. Navigate to project directory
3. Build project: `mvn clean compile`
4. Deploy to Tomcat server
5. Access application: `http://localhost:8080/engineering-mgmt`

## 📊 Version Control & Workflows
### Git Practices
- **Branching Strategy:** Feature-based branching
- **Commit Messages:** Clear, descriptive commits
- **Version Tags:** Major milestone tagging
- **Documentation:** Comprehensive commit history

### Development Workflow
1. Feature development in separate branches
2. Code review process
3. Testing before merge
4. Deployment automation

## 📈 System Validation
### Quality Assurance
- Input validation mechanisms
- Error handling procedures
- Security best practices
- Performance optimization

### Reports & Analytics
- User activity reports
- System performance metrics
- Database query optimization
- Usage statistics

## 🔒 Security Features
- Password encryption
- Session timeout management
- SQL injection prevention
- XSS protection measures

## 📚 Documentation Standards
- Code commenting conventions
- API documentation
- User manual guidelines
- Technical specification documents

## 🎯 Project Achievements
This project successfully demonstrates:
- Professional software engineering practices
- Modern web application development
- Database design and optimization
- Version control and collaboration
- Testing methodologies and quality assurance

## 📞 Support & Contact
For technical inquiries or project clarifications, please refer to the comprehensive documentation provided in the `/docs` folder or contact through the university portal.

---
**Note:** This project represents academic work completed as part of the CIS6003 Advanced Programming module, focusing on engineering skills demonstration rather than complex industrial-level implementation.
