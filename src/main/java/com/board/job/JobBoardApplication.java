package com.board.job;

import com.board.job.model.entity.*;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.board.job.mongoDB.MongoClientConnection;
import com.board.job.service.*;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.candidate.CandidateProfileService;
import com.board.job.service.employer.EmployerCompanyService;
import com.board.job.service.employer.EmployerProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class JobBoardApplication implements CommandLineRunner {
    private static final String CONNECTION_MONGO = System.getenv("mongoDB_url");

    private final RoleService roleService;
    private final UserService userService;
    private final CandidateProfileService candidateProfileService;
    private final CandidateContactService candidateContactService;
    private final EmployerProfileService employerProfileService;
    private final EmployerCompanyService employerCompanyService;
    private final VacancyService vacancyService;
    private final MessengerService messengerService;
    private final FeedbackService feedbackService;
    private final ImageService imageService;
    private final PDFService pdfService;

    private static void setToProperties() {
        String username = System.getenv("username");
        String password = System.getenv("password");

        if (Objects.isNull(username) || Objects.isNull(password)) {
            log.warn("You need to write your username and password in Environment Variables!");
            return;
        }

        getAndSetProperties(username, password);
    }

    private static void getAndSetProperties(String username, String password) {
        Properties properties = new Properties();
        String filePath = "src/main/resources/application.properties";

        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
            inputStream.close();

            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/job_board");
            properties.setProperty("spring.data.mongodb.uri", CONNECTION_MONGO);

            FileOutputStream outputStream = new FileOutputStream(filePath);
            properties.store(outputStream, null);

            outputStream.flush();
            outputStream.close();

            log.info("Username and password was successfully written in file application.properties.");
        } catch (IOException ioException) {
            log.error("Error: writing in property file {}", ioException.getMessage());
        }
    }

    public static void main(String[] args) {
        setToProperties();
        MongoClientConnection.connectToDB(CONNECTION_MONGO);

        SpringApplication.run(JobBoardApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("SocialMediaApplication has been started!!!");

        createTables();

        log.info("All entities has been written to DB.");
    }

    private void createTables() {
        Role admin = roleService.create("ADMIN");
        Role user = roleService.create("USER");
        Role candidate = roleService.create("CANDIDATE");
        Role employer = roleService.create("EMPLOYER");

//     !!!!  USERS  !!!!
        User userAdmin = createUser("Admin", "Carol", "admin@mail.co", "1111", admin);
        User userLarry = createUser("Larry", "Jackson", "larry@mail.co", "2222", user);
        User userNikole = createUser("Nikole", "Jackson", "nikole@mail.co", "3333", user);
        User userDonald = createUser("Donald", "Carol", "donald@mail.co", "4444", user);
        User userHelen = createUser("Nikole", "Helen", "helen@mail.co", "5555", user);
        User userViolet = createUser("Violet", "Jackson", "violet@mail.co", "6666", user);

//     !!!!  CANDIDATES PROFILES  !!!!
        CandidateProfile candidateProfileAdmin = createCandidateProfile(userAdmin.getId(), "QA", 2500, 2.3,
                "Ukraine", "Dnipro", Category.QA, LanguageLevel.PRE_INTERMEDIATE, LanguageLevel.ADVANCED_FLUENT,
                Strings.getAdminExperienceExplanation(), Strings.getAdminAchievements());
        CandidateProfile candidateProfileNikole = createCandidateProfile(userNikole.getId(), "Middle Python Developer", 1500, 2,
                "Poland", "Krakow", Category.PYTHON, LanguageLevel.INTERMEDIATE, LanguageLevel.ADVANCED_FLUENT,
                Strings.getNikoleExperienceExplanation(), "");
        CandidateProfile candidateProfileDonald = createCandidateProfile(userDonald.getId(), "Senior C++ Developer", 5000, 6,
                "Ukraine", "Kyiv", Category.C_PLUS_PLUS, LanguageLevel.UPPER_INTERMEDIATE, LanguageLevel.UPPER_INTERMEDIATE,
                Strings.getDonaldExperienceExplanation(), Strings.getDonaldAchievements());
        CandidateProfile candidateProfileHelen = createCandidateProfile(userHelen.getId(), "Trainee Java Developer", 300, 0,
                "Poland", "Warsaw", Category.JAVA, LanguageLevel.BEGINNER_ELEMENTARY, LanguageLevel.NO,
                Strings.getHelenExperienceExplanation(), "");

//     !!!! CANDIDATES CONTACTS !!!!
        CandidateContact candidateContactAdmin = createCandidateContacts(userAdmin.getId(), "0798764532",
                "@admin.telegram", "www.linkedin.co/AdminkoDKFfijif", "www.github.co/adminious",
                "www.portfolio.co/CarolsFKfkffk");

        CandidateContact candidateContactNikole = createCandidateContacts(userNikole.getId(), "0603457012",
                "@nikoliasias", "www.linkedin.co/lfpeflefepff", "www.github.co/NikoleStrike",
                "www.portfolio.co/12KMkfmkgmf");

        CandidateContact candidateContactDonald = createCandidateContacts(userDonald.getId(), "0560987654",
                "@donaldc++", "www.linkedin.co/donaldino", "www.github.co/DonaldC++",
                "www.portfolio.co/pkgprkrogkokg");

        CandidateContact candidateContactHelen = createCandidateContacts(userHelen.getId(), "0670987865",
                "@beginner_developer", "www.linkedin.co/ODOJFOGG", "www.github.co/Helen_Begin",
                "www.portfolio.co/:DKDKojfj2434");

//        userService.delete(userAdmin.getId());
//        log.info("AFTER CREATING CANDIDATES CONTACTS DELETE FINISHED SUCCESSFULLY");

//     !!!! EMPLOYER PROFILES !!!!
        EmployerProfile employerProfileAdmin = createEmployerProfile(userAdmin.getId(), "HR manager", "SoftServe",
                "@admin.telegram", "0798764532", "www.linkedin.co/AdminkoDKFfijif");

        EmployerProfile employerProfileLarry = createEmployerProfile(userLarry.getId(), "Lead Engineer", "Technologies",
                "@larriesfun", "0456879809", "www.linkedin.co/KOFjfeojfoLarry");

        EmployerProfile employerProfileViolet = createEmployerProfile(userViolet.getId(), "CEO", "Wikipedia",
                "@violettoni", "0566542310", "www.linkedin.co/DEKFMfemf");

//      !!!! EMPLOYER COMPANIES !!!!
        EmployerCompany employerCompanyAdmin = createEmployerCompany(userAdmin.getId(), Strings.aboutSoftServe(),
                "www.softserve.co");

        EmployerCompany employerCompanyLarry = createEmployerCompany(userLarry.getId(), Strings.aboutTechnologies(),
                "www.technologies.co.apps");

        EmployerCompany employerCompanyViolet = createEmployerCompany(userViolet.getId(), Strings.aboutWikipedia(),
                "www.wikipedia.co/team");

//      !!!! VACANCIES !!!!
        Vacancy vacancyPHPAdmin = createVacancy(userAdmin.getId(), "PHP developer", JobDomain.E_COMMERCE_MARKETPLACE,
                Strings.detailDescriptionAdminPHPVacancy(), Category.PHP, WorkMode.ONLY_REMOTE, "Ukraine", "",
                500, 1500, 1.5, LanguageLevel.INTERMEDIATE);

        Vacancy vacancyTypescriptAdmin = createVacancy(userAdmin.getId(), "Junior Typescript developer", JobDomain.MEDIA,
                Strings.detailDescriptionAdminTypescriptVacancy(), Category.TYPESCRIPT, WorkMode.CHOICE_OF_CANDIDATE,
                "Ukraine", "Kyiv", 400, 900, 0.5, LanguageLevel.INTERMEDIATE);

        Vacancy vacancyKotlinLarry = createVacancy(userLarry.getId(), "Senior Kotlin Developer", JobDomain.BIG_DATA,
                Strings.detailDescriptionLarryKotlinVacancy(), Category.KOTLIN, WorkMode.ONLY_OFFICE,
                "Ukraine", "Lviv", 2000, 4000, 5, LanguageLevel.PRE_INTERMEDIATE);

        Vacancy vacancyMiddleJavaLarry = createVacancy(userLarry.getId(), "Middle Java Developer", JobDomain.OTHER,
                Strings.detailDescriptionLarryMiddleJavaVacancy(), Category.JAVA, WorkMode.HYBRID,
                "Ukraine", "", 700, 2000, 1, LanguageLevel.PRE_INTERMEDIATE);

        Vacancy vacancyTraineeJavaLarry = createVacancy(userLarry.getId(), "Trainee Java Developer", JobDomain.OTHER,
                Strings.detailDescriptionLarryTraineeJavaVacancy(), Category.JAVA, WorkMode.ONLY_OFFICE,
                "Ukraine", "Lviv", 300, 500, 0, LanguageLevel.INTERMEDIATE);

        Vacancy vacancyCPlusPlusViolet = createVacancy(userViolet.getId(), "Tech Lead C++", JobDomain.BIG_DATA,
                Strings.detailDescriptionVioletCPlusPlusVacancy(), Category.C_PLUS_PLUS, WorkMode.CHOICE_OF_CANDIDATE,
                "Ukraine", "Kyiv", 3500, 7000, 6.5, LanguageLevel.PRE_INTERMEDIATE);

        Vacancy vacancyPythonViolet = createVacancy(userViolet.getId(), "Middle Python Developer", JobDomain.BIG_DATA,
                Strings.detailDescriptionVioletPythonVacancy(), Category.PYTHON, WorkMode.ONLY_REMOTE,
                "Ukraine", "", 1000, 2000, 1.7, LanguageLevel.INTERMEDIATE);

        Vacancy vacancyQAViolet = createVacancy(userViolet.getId(), "QA", JobDomain.OTHER,
                Strings.detailDescriptionVioletQAVacancy(), Category.QA, WorkMode.ONLY_OFFICE,
                "Ukraine", "Kyiv", 1000, 3000, 2, LanguageLevel.PRE_INTERMEDIATE);

        //      !!!! IMAGES !!!!
        Image imageAdminCandidate = createImageForCandidate(candidateContactAdmin.getId(), null);

        Image imageNikole = createImageForCandidate(candidateContactNikole.getId(), "files/photos/nicolas.jpg");

        Image imageDonald = createImageForCandidate(candidateContactDonald.getId(), "files/photos/donaldPicture.jpg");

        Image imageHelen = createImageForCandidate(candidateContactHelen.getId(), "files/photos/helenPicture.jpg");

        Image imageAdminEmployer = imageService.update(imageAdminCandidate.getId(), setContent("files/photos/adminPhoto.jpg"));

        Image imageLarry = createImageForEmployer(employerProfileLarry.getId(), null);

        Image imageViolet = createImageForEmployer(employerProfileViolet.getId(), "files/photos/violetPhoto.jpg");

//      !!!! PDF FILES !!!!
        PDF_File pdfAdmin = createPDF(candidateContactAdmin.getId(), "files/pdf/CV_Maksym_Korniev.pdf");

        PDF_File pdfNikole = createPDF(candidateContactNikole.getId(), "files/pdf/Certificate_Maksym_Korniev.pdf");

        PDF_File pdfDonald = createPDF(candidateContactDonald.getId(), "files/pdf/Certificate_Maksym_Korniev.pdf");

        PDF_File pdfHelen = createPDF(candidateContactHelen.getId(), null);

//      !!!! MESSENGERS !!!!
        long adminAndVioletCompany = createMessenger(vacancyQAViolet.getId(), candidateProfileAdmin.getId());

        long nikoleAndVioletCompany = createMessenger(vacancyPythonViolet.getId(), candidateProfileNikole.getId());

        long donaldAndVioletCompany = createMessenger(vacancyCPlusPlusViolet.getId(), candidateProfileDonald.getId());

        long helenAndLarryCompany = createMessenger(vacancyTraineeJavaLarry.getId(), candidateProfileHelen.getId());

//      !!!! FEEDBACKS !!!!
        Feedback feedbackAdmin = createFeedback(userAdmin.getId(), adminAndVioletCompany, Strings.textAdmin());

        Feedback feedbackNikole = createFeedback(userNikole.getId(), nikoleAndVioletCompany, Strings.textNikole());

        Feedback feedbackDonald = createFeedback(userDonald.getId(), donaldAndVioletCompany, Strings.textDonald());

        Feedback feedbackHelen = createFeedback(userHelen.getId(), helenAndLarryCompany, Strings.textHelen());

        Feedback feedbackVioletToAdmin = createFeedback(userViolet.getId(), adminAndVioletCompany, Strings.textViolet());

        Feedback feedbackVioletToNikole = createFeedback(userViolet.getId(), nikoleAndVioletCompany, Strings.textViolet());

        Feedback feedbackVioletToDonald = createFeedback(userViolet.getId(), donaldAndVioletCompany, Strings.textViolet());

        Feedback feedbackLarryToHelen = createFeedback(userLarry.getId(), helenAndLarryCompany, Strings.textViolet());
    }

    private User createUser(String firstName, String lastName, String email, String password, Role role) {
        var user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        user = userService.create(user, Set.of(role));
        log.info("User with name {} successfully created", user.getName());

        return user;
    }

    private CandidateProfile createCandidateProfile(long ownerId, String position, int salaryExpectation, double workExperience,
                                                    String countryOfResidence, String cityOfResidence, Category category,
                                                    LanguageLevel englishLevel, LanguageLevel ukrainianLevel,
                                                    String experienceExplanation, String achievements) {
        var candidateProfile = new CandidateProfile();
        candidateProfile.setPosition(position);
        candidateProfile.setSalaryExpectations(salaryExpectation);
        candidateProfile.setWorkExperience(workExperience);
        candidateProfile.setCountryOfResidence(countryOfResidence);
        candidateProfile.setCityOfResidence(cityOfResidence);
        candidateProfile.setCategory(category);
        candidateProfile.setEnglishLevel(englishLevel);
        candidateProfile.setUkrainianLevel(ukrainianLevel);
        candidateProfile.setExperienceExplanation(experienceExplanation);
        candidateProfile.setAchievements(achievements);

        candidateProfile = candidateProfileService.create(ownerId, candidateProfile);
        log.info("Candidate profile for user {} successfully created", candidateProfile.getOwner().getName());

        return candidateProfile;
    }

    private CandidateContact createCandidateContacts(long ownerId, String phone,
                                                     String telegram, String linkedIn, String githubUrl, String portfolioUrl) {

        var candidateContacts = new CandidateContact();
        candidateContacts.setPhone(phone);
        candidateContacts.setTelegram(telegram);
        candidateContacts.setLinkedInProfile(linkedIn);
        candidateContacts.setGithubUrl(githubUrl);
        candidateContacts.setPortfolioUrl(portfolioUrl);

        candidateContacts = candidateContactService.create(ownerId, candidateContacts);
        log.info("Candidate contacts for user {} successfully created", candidateContacts.getCandidateName());

        return candidateContacts;
    }

    private EmployerProfile createEmployerProfile(long ownerId, String positionInCompany, String companyName,
                                                  String telegram, String phone, String linkedIn) {

        var employerProfile = new EmployerProfile();
        employerProfile.setPositionInCompany(positionInCompany);
        employerProfile.setCompanyName(companyName);
        employerProfile.setTelegram(telegram);
        employerProfile.setPhone(phone);
        employerProfile.setLinkedInProfile(linkedIn);

        employerProfile = employerProfileService.create(ownerId, employerProfile);
        log.info("Employer profile for user {} successfully created", employerProfile.getEmployerName());

        return employerProfile;
    }

    private EmployerCompany createEmployerCompany(long ownerId, String aboutCompany, String webSite) {
        var employerCompany = new EmployerCompany();

        employerCompany.setAboutCompany(aboutCompany);
        employerCompany.setWebSite(webSite);

        employerCompany = employerCompanyService.create(ownerId, aboutCompany, webSite);
        log.info("Employer company for user {} successfully created", employerCompany.getOwner().getName());

        return employerCompany;
    }

    private Vacancy createVacancy(long ownerId, String lookingFor, JobDomain domain, String detailDescription,
                                  Category category, WorkMode workMode, String country, String city,
                                  int salaryFrom, int salaryTo, double workExperience, LanguageLevel englishLevel) {
        var vacancy = new Vacancy();

        vacancy.setLookingFor(lookingFor);
        vacancy.setDomain(domain);
        vacancy.setDetailDescription(detailDescription);
        vacancy.setCategory(category);
        vacancy.setWorkMode(workMode);
        vacancy.setCountry(country);
        vacancy.setCity(city);
        vacancy.setSalaryFrom(salaryFrom);
        vacancy.setSalaryTo(salaryTo);
        vacancy.setWorkExperience(workExperience);
        vacancy.setEnglishLevel(englishLevel);

        vacancy = vacancyService.create(ownerId, vacancy);
        log.info("Vacancy for user {} successfully created", vacancy.getEmployerCompany().getOwner().getName());

        return vacancy;
    }

    private long createMessenger(long vacancyId, long candidateProfileId) {
        var messenger = messengerService.create(vacancyId, candidateProfileId);
        log.info("Messenger between company {} and candidate {} successfully created",
                messenger.getVacancy().getEmployerProfile().getCompanyName(),
                messenger.getCandidateProfile().getOwner().getName());

        return messenger.getId();
    }

    private Feedback createFeedback(long ownerId, long messengerId, String text) {
        var feedback = feedbackService.create(ownerId, messengerId, text);
        log.info("Feedback for messenger {} successfully created", messengerId);

        return feedback;
    }

    private Image createImageForCandidate(long candidateId, String filename) {
        var image = imageService.createWithCandidate(candidateId, setContent(filename));
        log.info("Image for candidate {} successfully created", candidateId);

        return image;
    }

    private Image createImageForEmployer(long employerId, String filename) {
        var image = imageService.createWithEmployer(employerId, setContent(filename));
        log.info("Image for employer {} successfully created", employerId);

        return image;
    }

    private PDF_File createPDF(long candidateId, String filename) {
        var pdf = pdfService.create(candidateId, setContent(filename));
        log.info("PDF file for candidate {} successfully created", candidateId);

        return pdf;
    }

    private byte[] setContent(String filename) {
        if (filename != null) {
            try {
                return Files.readAllBytes(Path.of(filename));
            } catch (IOException io) {
                throw new IllegalArgumentException(String.format("File with name %s not found", filename));
            }
        }
        return null;
    }


    private static class Strings {
        private static String getAdminExperienceExplanation() {
            return """
                    I have been working in the field of Quality Assurance for the past 2.3 years, primarily focusing on testing software applications in various domains. 
                    My experience includes creating test plans, designing test cases, and executing both manual and automated tests to ensure the quality and functionality 
                    of software products. I am well-versed in using testing tools and frameworks to streamline the testing process, and I have a strong understanding of 
                    different testing methodologies, including regression, functional, and integration testing. Throughout my career, 
                    I have collaborated closely with development teams to identify
                    and report defects, providing them with detailed information to facilitate efficient issue resolution. 
                    My familiarity with Java has enabled me to develop automated test scripts, enhancing the overall efficiency of the QA process. 
                    Additionally, I have actively contributed to process improvement initiatives, optimizing testing procedures and enhancing overall product quality. 
                    I am excited to continue leveraging my QA expertise to ensure the delivery of reliable and high-quality software solutions.
                    """;
        }

        private static String getAdminAchievements() {
            return """
                    Established Efficient Testing Processes: Streamlined and optimized testing procedures, 
                    reducing testing time by 20% while maintaining high test coverage and accuracy.
                    Automated Test Suites: Developed automated test scripts using Java and testing frameworks, resulting 
                    in a 30% reduction in manual testing effort and quicker identification of critical issues.                  
                    Bug Advocacy: Identified and reported critical defects that led to improved software stability and a 
                    15% reduction in post-release issues reported by end-users.
                    """;
        }

        private static String getNikoleExperienceExplanation() {
            return """
                    As a Middle Python Developer with 2 years of experience, I have actively contributed to the design, 
                    development, and maintenance of software applications using Python. My experience includes working on 
                    both backend and data processing tasks, where I have implemented efficient algorithms and data structures
                    to optimize performance. I am proficient in utilizing Python libraries and frameworks, 
                    and I have successfully delivered solutions that meet both functional and performance requirements.
                    My experience with version control systems, such as Git, has allowed me to collaborate seamlessly
                    with cross-functional teams and manage code effectively. I have also gained hands-on experience with
                    database systems, including SQL and NoSQL databases, to store and retrieve data efficiently. 
                    Throughout my career, I have consistently demonstrated problem-solving skills, a strong attention to detail
                    , and an eagerness to learn and adapt to new technologies. 
                    I look forward to contributing my expertise to develop innovative and reliable software solutions.
                    """;
        }

        private static String getDonaldExperienceExplanation() {
            return """
                    With 6 years of dedicated experience as a Senior C++ Developer, I have been deeply involved in the 
                    design, development, and optimization of complex software systems. My journey in software 
                    engineering has seen me contribute to various domains, including high-performance applications, 
                    real-time systems, and embedded solutions. Throughout my career, I've demonstrated proficiency 
                    in modern C++ practices and have a strong understanding of memory management, multi-threading, 
                    and object-oriented design patterns. I've successfully led cross-functional teams and provided 
                    mentorship to junior developers, fostering a culture of collaboration and continuous improvement. 
                    My work often involves tackling performance bottlenecks and optimizing code for speed and efficiency. 
                    I'm well-versed in version control, code review processes, and have a proven track record of 
                    delivering high-quality code on time. As a Senior C++ Developer, I remain committed to pushing 
                    the boundaries of software development and finding innovative solutions to challenging problems.
                    """;
        }

        private static String getDonaldAchievements() {
            return """
                    High-Performance Application Development: 
                    Architected and implemented a high-performance real-time 
                    trading system, handling thousands of transactions per second with sub-millisecond latency, 
                    resulting in a 20% increase in trading efficiency.
                    Optimization Expertise: Led a performance optimization initiative that significantly reduced CPU 
                    usage by 30% and memory consumption by 25%, resulting in a more efficient and responsive software system.          
                    Complex Algorithm Implementation: 
                    Designed and coded complex algorithms for data analysis, enabling more accurate predictions and
                     insights, and leading to a 40% improvement in accuracy over previous solutions.                   
                    Cross-Platform Compatibility: 
                    Developed a cross-platform C++ library that seamlessly integrated with Windows, 
                    Linux and macOS, reducing development time for new platform releases by 50%.               
                    Real-Time Embedded Solutions: 
                    Created real-time embedded software for critical medical devices, meeting stringent safety
                     and regulatory requirements and contributing to a successful FDA approval.                 
                    Leadership and Mentorship: 
                    Mentored and guided a team of junior developers, resulting in enhanced coding standards,
                     improved teamwork, and the successful delivery of several major project milestones.
                    """;
        }

        private static String getHelenExperienceExplanation() {
            return """
                    As an aspiring Java Developer with a strong foundation in programming and a passion for technology,
                     I am eager to embark on a journey of professional growth. 
                     I have recently completed formal education in computer science, where I gained a solid 
                     understanding of core programming concepts and Java fundamentals. Throughout my studies, 
                     I have undertaken projects that have equipped me with hands-on experience in Java programming, 
                     including creating basic applications and implementing algorithms. I am familiar with 
                     object-oriented programming principles and have a basic grasp of data structures and algorithms. 
                     During my academic years, I also actively participated in coding competitions and collaborative 
                     projects, honing my problem-solving skills and teamwork abilities. I am excited to leverage my 
                     educational background, enthusiasm, and determination to contribute positively to a dynamic 
                     development team and to continue learning and growing as a Java Developer
                    """;
        }

        private static String aboutSoftServe() {
            return """
                    About Us:
                    SoftServe is a leading global technology company that partners with organizations to drive 
                    digital transformation and deliver innovative solutions. 
                    With over two decades of experience, we offer a broad range of services and expertise in 
                    areas such as software development, design, consulting, and technology strategy. 
                                   
                    Our Mission:
                    At SoftServe, our mission is to empower businesses to accelerate their digital journey and achieve 
                    greater success. We are dedicated to helping our clients embrace cutting-edge technologies, 
                    adapt to market trends, and create lasting value through digital innovation.
                                        
                    Expertise:
                    Our team of experts spans a diverse range of disciplines, including software engineering, 
                    data analytics, cloud computing, user experience design, and more. With a commitment to continuous 
                    learning and skill development, we stay at the forefront of technology trends to deliver the best 
                    solutions for our clients.
                                        
                    Global Presence:
                    Headquartered in Austin, Texas, SoftServe operates globally with a presence in multiple 
                    countries, serving clients across various industries such as healthcare, finance, retail, 
                    and more. Our global reach enables us to provide localized solutions and insights while delivering 
                    the same high-quality services that define SoftServe.
                                        
                    Culture and Values:
                    At SoftServe, we value collaboration, innovation, and creativity. Our culture is rooted 
                    in a passion for technology and a dedication to helping our clients succeed. We foster a 
                    supportive and inclusive environment where every team member's contributions are valued, 
                    and diverse perspectives are encouraged.
                    """;
        }

        private static String aboutTechnologies() {
            return """
                    About Us:
                    Technologies is a forward-thinking technology company that specializes in delivering innovative
                     solutions to businesses across various industries. With a passion for cutting-edge advancements 
                     and a dedication to excellence, we empower our clients to thrive in the digital age.
                                      
                    Our Mission:
                    At Technologies, our mission is to bridge the gap between technology and business success. 
                    We strive to create transformative solutions that drive efficiency, productivity, 
                    and growth for our clients. Our commitment to staying at the forefront of technology 
                    trends ensures that we provide solutions that make a lasting impact.
                                      
                    Expertise:
                    Our team of skilled professionals boasts expertise in diverse areas, including software development, 
                    artificial intelligence, data analytics, cloud computing, and more. We combine technical 
                    prowess with a deep understanding of business needs, allowing us to tailor solutions that solve 
                    real-world challenges.
                                      
                    Innovation Culture:
                    Innovation is at the heart of everything we do. We foster a culture that encourages curiosity, 
                    exploration, and creative problem-solving. Our collaborative environment enables our team to 
                    brainstorm and develop groundbreaking ideas that push the boundaries of what's possible.
                                      
                    Client-Centric Approach:
                    At Technologies, our clients are partners in progress. We collaborate closely to understand their 
                    unique goals and challenges, ensuring that our solutions align with their vision. Our customer-centric
                     approach means that we are dedicated to delivering value and building long-lasting relationships.
                    """;
        }

        private static String aboutWikipedia() {
            return """
                    About Us:
                    Wikipedia is a globally renowned collaborative online encyclopedia that empowers individuals
                     around the world to share and access knowledge freely. With a commitment to 
                     open knowledge and democratized information, we have become a cornerstone of the digital age.
                                       
                    Our Mission:
                    At Wikipedia, our mission is to make knowledge accessible to everyone. We believe in the power of
                     collective intelligence and community-driven content creation. We aim to provide accurate 
                     and comprehensive information on a wide range of topics, contributing to a better-informed world.
                                                                                                                        
                    Global Impact:
                    Wikipedia is more than just a website—it's a movement. Our platform is available in hundreds 
                    of languages and serves as a valuable resource for students, researchers, professionals, 
                    and curious minds alike. With millions of articles contributed by volunteers, we embody the spirit 
                    of shared learning.
                                                                                                                        
                    Collaborative Community:
                    Our community of editors, writers, and contributors spans the globe. 
                    We are united by a common passion for knowledge and a dedication to maintaining the quality 
                    and accuracy of the content we provide. Working together, we create a diverse and inclusive information hub.
                                                                                                                        
                    Open Source Philosophy:
                    Wikipedia operates on an open-source philosophy, meaning that our content is freely accessible 
                    and can be edited by anyone with the intention to improve it. This ethos of collaboration has 
                    shaped the way we approach learning and the spread of information.
                    """;
        }

        private static String detailDescriptionAdminPHPVacancy() {
            return """
                    About SoftServe:
                    SoftServe is a leading global software development company with a rich history of delivering 
                    innovative solutions to clients across various industries. With a strong emphasis on technology, 
                    innovation, and expertise, SoftServe provides a dynamic and collaborative environment for 
                    professionals to excel and contribute to cutting-edge projects.
                                     
                    Job Description:
                    We are seeking a skilled PHP Developer to join our dynamic team at SoftServe. As a PHP Developer,
                     you will play a vital role in the design, development, and maintenance of web applications 
                     that are crucial to our clients' success. You will have the opportunity to work on exciting 
                     projects, collaborate with cross-functional teams, and contribute to the growth of our organization.
                                     
                    Responsibilities:            
                    Develop and maintain high-quality PHP applications and web solutions.
                    Collaborate with designers and front-end developers to create seamless user experiences.
                    Troubleshoot and debug issues to ensure optimal performance and functionality.
                    Participate in code reviews to maintain code quality and standards.
                    Contribute to architectural discussions and technical design decisions.
                    Stay updated with the latest trends and best practices in PHP development.
                                        
                    Requirements:                
                    Minimum of 1.5 years of professional experience in PHP development.
                    Proficiency in PHP, MySQL, HTML, CSS, and JavaScript.
                    Experience with popular PHP frameworks such as Laravel or Symfony.
                    Familiarity with version control systems (Git, SVN).
                    Strong problem-solving skills and attention to detail.
                    Excellent communication and teamwork skills.
                    Bachelor's degree in Computer Science, Engineering, or related field is a plus.
                                        
                    What We Offer: 
                    Competitive salary in the range of $500 - $1500 based on experience and skillset.
                    Opportunities for professional growth and career advancement.
                    Access to cutting-edge technologies and projects.
                    Collaborative and innovative work environment.
                    Comprehensive benefits package, including health insurance and more.
                    """;
        }

        private static String detailDescriptionAdminTypescriptVacancy() {
            return """
                    About Us:
                    SoftServe is a leading global technology company that specializes in software development
                     and consulting services. With over 15 years of experience, we are committed to 
                     delivering innovative solutions that empower businesses to succeed in the digital age. 
                     Our talented team of professionals collaborates with clients across various industries to 
                     create transformative software solutions.
                                                                                        
                    Job Description:                                                                  
                    We are seeking a motivated and passionate Junior TypeScript Developer to join our dynamic team. 
                    As a Junior TypeScript Developer, you will have the opportunity to work on exciting projects, 
                    collaborate with experienced developers, and contribute to the creation of cutting-edge web 
                    applications. This role is ideal for individuals who are looking to expand their skills and gain 
                    hands-on experience in a supportive and collaborative environment.
                                                                                        
                    Responsibilities:                                                                   
                    Collaborate with senior developers and project teams to design, develop, and implement web applications using TypeScript.
                    Write clean, maintainable, and efficient code while following best practices and coding standards.
                    Assist in the development and maintenance of front-end interfaces and components.
                    Participate in code reviews, debugging, and troubleshooting to ensure high-quality software.
                    Learn and apply new technologies and frameworks to enhance your development skills.
                    Contribute to the entire software development lifecycle, from requirements gathering to deployment and testing.
                                        
                    Qualifications:                                                             
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    Minimum of 0.5 years of experience in software development, with a focus on TypeScript or JavaScript.
                    Strong understanding of front-end development principles, including HTML, CSS, and responsive design.
                    Familiarity with modern web development frameworks such as Angular, React, or Vue.js.
                    Experience with version control systems (Git) and code collaboration tools.
                    Solid problem-solving skills and the ability to learn new technologies quickly.
                    Effective communication skills and the ability to work well within a team.
                                        
                    Benefits:                                                               
                    Competitive salary within the range of $400 - $900 per month, based on experience and skills.
                    Opportunities for professional growth and career advancement.
                    Access to training and workshops to enhance your technical skills.
                    Collaborative and inclusive work environment that fosters learning and innovation.
                    Flexible working hours and remote work options.
                    Comprehensive benefits package, including health insurance, paid time off, and more.
                    """;
        }

        private static String detailDescriptionLarryKotlinVacancy() {
            return """
                    About Us:       
                    Technologies Company is a forward-thinking technology firm dedicated to pushing the boundaries 
                    of innovation. With a proven track record of 20 years in the industry, we are at the forefront
                     of delivering cutting-edge software solutions that drive business success. Our team of passionate 
                     professionals is committed to creating transformative digital experiences for our clients.
                                        
                    Job Description:               
                    We are looking for a highly skilled and experienced Senior Kotlin Developer to join 
                    our dynamic team. As a Senior Kotlin Developer, you will play a pivotal role in 
                    designing and implementing complex software solutions using Kotlin and related technologies. 
                    This position offers an exciting opportunity to contribute to meaningful projects and collaborate 
                    with talented professionals.
                                        
                    Responsibilities:          
                    Lead the design, development, and implementation of software applications using Kotlin and other relevant technologies.
                    Collaborate with cross-functional teams, including product managers, designers, and other developers, to deliver high-quality solutions.
                    Mentor and guide junior developers, providing technical leadership and sharing best practices.
                    Participate in code reviews and ensure adherence to coding standards and best practices.
                    Optimize application performance and ensure scalability for future growth.
                    Stay up-to-date with industry trends and emerging technologies, applying them to enhance development processes.
                    Contribute to architectural decisions and technical discussions within the team.
                                        
                    Qualifications:                 
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    Minimum of 5 years of professional software development experience, with a strong focus on Kotlin.
                    Extensive experience with Kotlin programming language and a deep understanding of its features and capabilities.
                    Proficiency in working with modern software development frameworks, libraries, and tools.
                    Solid understanding of software architecture, design patterns, and best practices.
                    Strong problem-solving skills and the ability to tackle complex technical challenges.
                    Proven track record of successfully delivering high-quality software projects on time.
                    Excellent communication skills and the ability to collaborate effectively within a team environment.
                                        
                    Benefits:                 
                    Competitive salary within the range of $2000 - $4000 per month, based on experience and skills.
                    Opportunities for professional growth and advancement within a forward-thinking technology company.
                    Access to ongoing training, workshops, and conferences to enhance your technical skills.
                    Collaborative and innovative work culture that encourages creativity and continuous learning.
                    Flexible working hours and remote work options.
                    Comprehensive benefits package, including health insurance, paid time off, and more.
                    """;
        }

        private static String detailDescriptionLarryMiddleJavaVacancy() {
            return """
                    About Us:              
                    Technologies Company is an innovative tech firm known for its commitment to delivering high-quality
                     software solutions. With 20 years of experience, we have a proven track record of excellence 
                     in the industry. Our team of dedicated professionals thrives on creativity, collaboration,
                     and pushing the boundaries of technology to drive positive change.
                                       
                    Job Description:            
                    We are seeking a talented Middle Java Developer to join our dynamic team. As a Middle Java 
                    Developer, you'll have the opportunity to work on exciting projects and contribute to the growth 
                    of our software solutions. This role offers a balance between collaborative on-site work and remote 
                    flexibility, allowing you to showcase your skills and learn from experienced peers.
                                       
                    Responsibilities:             
                    Develop and maintain high-quality Java applications, ensuring adherence to coding standards and best practices.
                    Collaborate with cross-functional teams, including designers, product managers, and other developers, to deliver successful projects.
                    Participate in the entire software development lifecycle, from requirement analysis to deployment and maintenance.
                    Contribute to architectural decisions and technical discussions within the team.
                    Troubleshoot and resolve software defects, optimizing application performance.
                    Stay updated with the latest Java technologies, trends, and emerging tools.
                    Continuously enhance your technical skills and share knowledge within the team.
                    Embrace a hybrid work mode, alternating between on-site and remote work as needed.
                                        
                    Qualifications:                  
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    Minimum of 1 year of professional experience in Java development.
                    Solid understanding of Java programming language and its core concepts.
                    Experience with Java frameworks such as Spring or Hibernate.
                    Familiarity with database systems and SQL queries.
                    Strong problem-solving skills and a passion for learning new technologies.
                    Excellent teamwork and communication skills.
                    Ability to work independently and collaborate effectively in a remote work setting.
                                        
                    Benefits:             
                    Competitive salary within the range of $700 - $2000 per month, based on experience and skills.
                    Hybrid work mode offering flexibility between on-site and remote work.
                    Opportunity to work on innovative projects and make a meaningful impact.
                    Professional growth and skill enhancement through ongoing training and workshops.
                    Collaborative work culture that values diversity and fosters creativity.
                    Comprehensive benefits package, including health insurance, paid time off, and more.
                    """;
        }

        private static String detailDescriptionLarryTraineeJavaVacancy() {
            return """
                    About Us:
                    Technologies Company is a forward-thinking tech firm renowned for its commitment to innovation 
                    and cutting-edge solutions. With a history of 20 years in the industry, we pride ourselves 
                    on fostering a collaborative environment where learning and growth thrive. As a Trainee Java 
                    Developer, you'll have the chance to kick-start your career in the world of software development 
                    with a supportive and dynamic team.
                                                                                                        
                    Job Description:
                    We are excited to offer an opportunity for a passionate and driven Trainee Java Developer to join 
                    our team. This position is ideal for individuals who are enthusiastic about starting their career 
                    in Java development and are eager to learn from experienced professionals. As a Trainee 
                    Java Developer, you will receive hands-on training, mentorship, and exposure to real-world projects 
                    that will equip you with the skills needed for a successful career in software development.
                                                                                                        
                    Responsibilities:
                    Collaborate with senior developers and project teams to gain practical experience in Java development.
                    Participate in coding, testing, debugging, and maintaining software applications.
                    Learn and apply best practices in Java programming, code documentation, and version control.
                    Contribute to team discussions and brainstorming sessions to solve technical challenges.
                    Embrace a supportive learning environment and actively seek feedback to improve your skills.
                    Work closely with cross-functional teams to understand project requirements and deliver solutions.
                    Attend training sessions and workshops to enhance your technical skills and knowledge.
                    Embrace the office-based work mode and engage in daily team activities and interactions.
                                        
                    Qualifications:
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    No prior professional experience required; fresh graduates and entry-level candidates are welcome to apply.
                    Basic understanding of programming concepts and a strong interest in Java development.
                    Eagerness to learn and a proactive attitude towards acquiring new skills.
                    Good communication skills and the ability to collaborate effectively within a team.
                    Passion for technology and a strong desire to grow as a software developer.
                                        
                    Benefits:
                    Competitive monthly salary within the range of $300 - $500, commensurate with experience and skills.
                    Hands-on training and mentorship from experienced developers to accelerate your learning curve.
                    Exposure to real-world projects, allowing you to apply your skills in practical scenarios.
                    Opportunity to kick-start your career in software development in a supportive environment.
                    Professional growth and skill enhancement through training, workshops, and learning resources.
                    Collaborative work culture that values creativity, teamwork, and innovation.
                    """;
        }

        private static String detailDescriptionVioletCPlusPlusVacancy() {
            return """
                    About Us:
                    Wikipedia Company is a dynamic and globally recognized organization that serves as a hub of 
                    knowledge and innovation. Our commitment to open-source principles and collaborative contributions 
                    has transformed the way people access information. As a Tech Lead - C++, you'll play a pivotal role 
                    in driving the development of impactful software solutions that power one of the most visited 
                    websites worldwide.
                                                                                                 
                    Job Description:
                    We are seeking a highly skilled and motivated Tech Lead - C++ to lead and mentor our C++ 
                    development team. This role involves not only hands-on technical expertise but also strong 
                    leadership and communication skills. As a Tech Lead, you will guide the team through the design, 
                    development, and maintenance of C++ applications critical to the performance and scalability of 
                    Wikipedia's platform.
                                                                                                 
                    Responsibilities:
                    Lead a team of skilled C++ developers, fostering a collaborative and innovative work environment.
                    Provide technical guidance, mentorship, and code reviews to ensure high-quality software development.
                    Collaborate with cross-functional teams to design, develop, and maintain C++ applications.
                    Architect scalable and efficient solutions that meet performance and security requirements.
                    Identify and resolve technical challenges, ensuring timely delivery of projects.
                    Stay up-to-date with industry trends and emerging technologies to drive innovation.
                    Participate in design discussions, code reviews, and contribute to the software development life cycle.
                    Act as a subject matter expert in C++ development, advocating for best practices.
                    Collaborate with stakeholders to gather and analyze requirements for new features.
                    Drive continuous improvement in coding practices, development processes, and tools.
                                        
                    Qualifications:
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    Minimum of 6.5 years of hands-on experience in C++ software development.
                    Proven experience leading software development teams and mentoring team members.
                    Strong understanding of software architecture, design patterns, and object-oriented principles.
                    Experience with performance optimization, memory management, and debugging.
                    Excellent communication skills, with the ability to communicate complex technical concepts.
                    Ability to collaborate effectively with cross-functional teams and stakeholders.
                    Passion for open-source development and contributing to the broader community.
                    Prior experience in the tech industry or with online platforms is a plus.
                                        
                    Benefits:
                    Competitive monthly salary within the range of $3500 - $7000, based on experience and expertise.
                    Opportunity to lead a team of talented C++ developers and drive impactful projects.
                    Flexibility to work on-site or remotely, depending on your preference and location.
                    Exposure to cutting-edge technologies and contributions to open-source projects.
                    Collaborative work environment that encourages innovation and knowledge sharing.
                    Professional growth and skill enhancement through continuous learning opportunities.
                    Impactful role within Wikipedia Company, influencing the development of widely used platforms.
                    """;
        }

        private static String detailDescriptionVioletPythonVacancy() {
            return """
                    About Us:
                    Wikipedia Company is a trailblazing organization that thrives on knowledge, collaboration, 
                    and innovation. With a rich history of 18 years in shaping online knowledge-sharing platforms,
                     we are committed to creating and maintaining reliable, free-access content for millions worldwide.
                      As a Middle Python Developer, you'll play a pivotal role in advancing our technology landscape 
                      and contributing to the global pursuit of information.
                                                                                                                   
                    Job Description:
                    We are excited to welcome a talented Middle Python Developer to our team. In this role, 
                    you will be instrumental in building and enhancing the technology that powers one of 
                    the world's largest collaborative platforms. You'll work on projects that directly impact 
                    our users' experience and contribute to the ongoing success of Wikipedia and its sister projects.
                                                                                                                   
                    Responsibilities:
                    Collaborate with cross-functional teams to develop and maintain Python-based applications, tools, and systems.
                    Write clean, efficient, and maintainable code while adhering to best practices and coding standards.
                    Participate in designing, implementing, and testing software solutions that improve user experiences and functionality.
                    Troubleshoot and debug issues, identifying root causes and implementing effective solutions.
                    Work closely with product managers, designers, and fellow developers to plan and execute software projects.
                    Contribute to the continuous improvement of development processes and tools.
                    Stay updated with emerging trends and technologies in the Python development ecosystem.
                    Provide mentorship and guidance to junior developers when necessary.
                                        
                    Qualifications:
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    1.7 years of professional experience as a Python developer, with a proven track record of delivering successful projects.
                    Strong proficiency in Python programming and familiarity with relevant frameworks (e.g., Django, Flask).
                    Experience with version control systems (e.g., Git) and collaborative development workflows.
                    Solid understanding of software development concepts, including debugging, testing, and code review.
                    Strong problem-solving skills and the ability to analyze complex technical challenges.
                    Excellent communication skills and the ability to work effectively within a team.
                    Passion for open-source projects and contributing to the global knowledge-sharing community.
                                        
                    Benefits:
                    Competitive monthly salary within the range of $1000 - $2000, commensurate with experience and skills.
                    Opportunity to contribute to a world-renowned platform dedicated to free knowledge sharing.
                    Exposure to challenging projects that impact millions of users around the globe.
                    Collaborative work environment that encourages innovation, diversity, and inclusion.
                    Professional growth and skill development through mentorship and continuous learning opportunities.
                    Access to cutting-edge tools and technologies to enhance your development capabilities.
                    """;
        }

        private static String detailDescriptionVioletQAVacancy() {
            return """
                    About Us:
                    Wikipedia Company is a globally recognized technology organization dedicated to providing 
                    accurate and reliable information to millions of users around the world. With a rich history of 
                    empowering knowledge seekers, we are seeking a skilled and passionate Quality Assurance Engineer to 
                    join our team. As a QA professional, you will play a vital role in ensuring the quality and 
                    reliability of our platforms, enabling users to access trustworthy information.
                                       
                    Job Description:
                    We are excited to welcome a talented and experienced Quality Assurance Engineer to our 
                    team. As a QA Engineer at Wikipedia Company, you will be responsible for ensuring that our platforms 
                    and applications meet the highest standards of quality, accuracy, and functionality. 
                    You will collaborate with cross-functional teams to test, validate, and enhance the user 
                    experience, contributing to the integrity of the information shared on our platforms.
                                       
                    Responsibilities:         
                    Collaborate with development and product teams to understand project requirements and user stories.
                    Design, develop, and execute comprehensive test plans, test cases, and test scripts for web and mobile applications.
                    Conduct functional, regression, and performance testing to identify defects and ensure optimal user experience.
                    Participate in the identification and documentation of software defects and track their resolution process.
                    Perform exploratory testing to uncover hidden issues and ensure the reliability of our platforms.
                    Contribute to the continuous improvement of QA processes, methodologies, and best practices.
                    Automate test cases using testing frameworks and tools to streamline the testing process.
                    Monitor and report testing progress, results, and metrics to project stakeholders.
                    Collaborate with cross-functional teams to provide input on user experience improvements and feature enhancements.
                    Provide mentorship and guidance to junior QA team members when necessary.
                                       
                    Qualifications:                   
                    Bachelor's degree in Computer Science, Software Engineering, or a related field.
                    Minimum of 2 years of professional experience in software quality assurance.
                    Strong understanding of QA methodologies, best practices, and testing techniques.
                    Experience with manual and automated testing of web and mobile applications.
                    Proficiency in writing test plans, test cases, and test scripts to ensure comprehensive coverage.
                    Familiarity with testing frameworks and tools for automated testing (e.g., Selenium, JUnit).
                    Detail-oriented mindset with the ability to identify, document, and track defects effectively.
                    Excellent problem-solving skills and a proactive approach to identifying potential issues.
                    Strong communication skills and the ability to collaborate with cross-functional teams.
                                       
                    Benefits:          
                    Competitive monthly salary within the range of $1000 - $3000, based on experience and skills.
                    Opportunity to work with a globally recognized company dedicated to providing accurate and reliable information.
                    Collaborative work environment that values knowledge sharing and innovation.
                    Professional growth and development opportunities through training and skill enhancement.
                    Impactful role contributing to the quality and reliability of platforms used by millions of users.
                    Comprehensive benefits package including health insurance, retirement plans, and more.
                    """;
        }

        private static String textAdmin() {
            return """
                    I recently came across the Quality Assurance (QA) vacancy at Wikipedia Company and felt compelled to
                     share my thoughts on the opportunity. As someone who aligns with the company's values and the
                     role's requirements, I believe this is a perfect match for my career aspirations.
                                        
                    The fact that the work mode is exclusively office-based resonates with me. I value the 
                    collaborative environment that an office setting offers, allowing for real-time communication 
                    with team members and fostering a strong sense of unity. The dedication to maintaining a quality 
                    standard while working in an office environment is truly commendable.
                                        
                    The offered salary range of $1000 - $3000 is not only competitive but also reflects 
                    the importance that Technologies Company places on recognizing the skill and effort of their QA 
                    professionals. It reassures me that my contributions would be duly acknowledged and rewarded, 
                    further motivating me to excel in my role.
                                        
                    With 2 years of work experience in the QA field, I believe I can seamlessly transition into this 
                    position. My track record includes successfully designing and executing test plans, identifying 
                    and reporting defects, and ensuring the overall quality of software products. I'm confident that 
                    my experience will allow me to make an immediate impact and contribute positively to the team's goals.
                                        
                    The pre-intermediate language level requirement aligns with my proficiency. I understand the 
                    importance of effective communication within a team and with other stakeholders, and I'm committed 
                    to improving my language skills to achieve greater fluency.
                                        
                    Considering my experience, skill set, and alignment with the role's requirements, my salary 
                    expectation of $2500 feels reasonable. I believe this compensation accurately reflects my value 
                    as an experienced QA professional who is dedicated to maintaining high standards and delivering 
                    quality results.
                                        
                    In conclusion, the QA vacancy at Technologies Company speaks directly to my career goals and 
                    aspirations. I appreciate the company's commitment to maintaining quality, the competitive 
                    salary range, and the focus on fostering a collaborative office environment. I am excited about 
                    the opportunity to contribute my expertise to a team that shares my passion for excellence.
                                        
                    Thank you for considering my application. I look forward to the possibility of becoming a valued 
                    member of the Technologies Company QA team.
                                        
                    Sincerely,
                    Maksym Carol
                    """;
        }

        private static String textNikole() {
            return """
                    As a seasoned Middle Python Developer with over 2 years of experience, I find the opportunity
                     presented by Wikipedia for a remote role incredibly appealing. The flexibility of only remote work
                      mode aligns perfectly with my current work-life balance needs, allowing me to contribute 
                      effectively while staying connected with my family.
                                        
                    Wikipedia's commitment to remote work and its reputation as a reliable source of 
                    knowledge further motivate me to be a part of the team. The offered salary range of $1000 - $2000 
                    reflects a competitive compensation for a Middle Python Developer role, and it's certainly in line 
                    with my salary expectations.
                                        
                    My journey as a Python developer has been rewarding, and my intermediate language skills 
                    complement the role's requirement. I'm excited about the prospect of collaborating with a diverse 
                    team of professionals to enhance Wikipedia's technical offerings.
                                        
                    Overall, this vacancy seems like a perfect opportunity for me to continue growing as a 
                    developer while contributing to Wikipedia's mission
                    """;
        }

        private static String textDonald() {
            return """
                    I recently came across the Tech Lead C++ vacancy at Wikipedia and was immediately intrigued by the 
                    challenging role it presents. With my background as a Senior C++ Developer and a strong passion for 
                    leading teams, I believe this opportunity aligns perfectly with my career aspirations.
                                        
                    The salary range of $3500 - $7000 is highly competitive and showcases Wikipedia's commitment to 
                    valuing the skills and expertise of their employees. The flexibility to choose the work mode is an 
                    appealing feature that emphasizes the company's recognition of individual preferences and needs.
                                        
                    Having worked as a Senior C++ Developer for 6 years, I'm excited about the prospect of stepping 
                    into a leadership role as a Tech Lead. Wikipedia's emphasis on 6.5 years of work experience for 
                    this position further demonstrates their dedication to finding candidates with a solid foundation 
                    in the field.
                                        
                    The language level requirement of Pre-Intermediate aligns with my current skill set, and I'm 
                    confident in my ability to communicate effectively with the team and stakeholders. As a Senior C++ 
                    Developer, my language level is at the Upper Intermediate level, which I believe will enhance my 
                    ability to collaborate and lead effectively.
                                        
                    I highly appreciate Wikipedia's commitment to fostering an environment of continuous learning 
                    and growth. This Tech Lead position presents an ideal opportunity for me to take on greater 
                    responsibilities, mentor team members, and contribute to meaningful projects that impact a global audience.
                                        
                    In summary, the Tech Lead C++ vacancy at Wikipedia resonates with my career goals, experience, 
                    and skill set. I look forward to the opportunity to further discuss how my background aligns 
                    with Wikipedia's values and requirements for this role.
                                        
                    Thank you for considering my application.
                                        
                    Sincerely,
                    Donald Carol
                    """;
        }

        private static String textHelen() {
            return """
                    I had the privilege of coming across the Trainee Java Developer vacancy at Technologies Company, 
                    and I must say, it was an exciting opportunity that perfectly aligned with my aspirations. 
                    The company's commitment to fostering talent and providing a supportive environment was evident 
                    from the very beginning.
                                        
                    As someone with a strong passion for Java programming and a determination to kick-start my career 
                    in software development, I was thrilled to find a role that welcomed applicants with a beginner to 
                    elementary language level. The fact that the position was tailored for individuals like me who are 
                    just starting their journey in the tech industry was truly encouraging.
                                        
                    The prospect of working in an only-office work mode appealed to me, as I believe that being in a 
                    collaborative and structured environment will accelerate my learning and skill development. 
                    The Technologies Company's emphasis on on-site engagement further demonstrates their dedication 
                    to nurturing trainees like myself.
                                        
                    One of the most appealing aspects of the Trainee Java Developer vacancy was the salary range of 
                    $300 to $500. This reflects the company's commitment to fair compensation for entry-level positions, 
                    which is commendable. For someone like me who is stepping into the professional world, this salary range 
                    is not only competitive but also motivating.
                                        
                    Moreover, the fact that the role requires 0 years of work experience is a testament to the 
                    company's willingness to invest in and mentor fresh talent. The opportunities for growth and 
                    development are clearly evident, and I am confident that Technologies Company will provide the 
                    guidance needed to succeed in the fast-paced world of Java development.
                                        
                    In conclusion, the Trainee Java Developer vacancy at Technologies Company embodies everything 
                    I was looking for in a first step towards a rewarding career in software development. The company's 
                    commitment to supporting junior developers, providing valuable work experience, and offering fair 
                    compensation is truly impressive. I am excited about the prospect of contributing to the company's 
                    success while building a strong foundation for my own professional journey.
                                        
                    Nikole Helen
                    """;
        }

        private static String textViolet() {
            return "Okay, thank you, we will check your application and get back to you later.";
        }
    }
}
