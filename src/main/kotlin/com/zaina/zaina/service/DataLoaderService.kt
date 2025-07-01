package com.zaina.zaina.service

import com.zaina.zaina.entity.*
import com.zaina.zaina.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class DataLoaderService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val cohortRepository: CohortRepository,
    private val eventRepository: EventRepository,
    private val resourceRepository: ResourceRepository,
    private val userEventRepository: UserEventRepository,
    private val messageRepository: MessageRepository,
    private val connectionRepository: ConnectionRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) {
            loadMockData()
        }
    }

    private fun loadMockData() {
        println("🚀 Starting comprehensive data loading...")
        
        // Create cohorts - Past, Current, and Future
        val cohort2022 = Cohort(
            name = "RISE 2022",
            startDate = LocalDate.of(2022, 1, 15),
            endDate = LocalDate.of(2022, 12, 15)
        )
        
        val cohort2023 = Cohort(
            name = "RISE 2023",
            startDate = LocalDate.of(2023, 1, 15),
            endDate = LocalDate.of(2023, 12, 15)
        )
        
        val cohort2024 = Cohort(
            name = "RISE 2024",
            startDate = LocalDate.of(2024, 1, 15),
            endDate = LocalDate.of(2024, 12, 15)
        )
        
        val cohort2025 = Cohort(
            name = "RISE 2025",
            startDate = LocalDate.of(2025, 1, 15),
            endDate = LocalDate.of(2025, 12, 15)
        )
        
        val savedCohort2022 = cohortRepository.save(cohort2022)
        val savedCohort2023 = cohortRepository.save(cohort2023)
        val savedCohort2024 = cohortRepository.save(cohort2024)
        val savedCohort2025 = cohortRepository.save(cohort2025)

        println("✅ Created 4 cohorts (2022, 2023, 2024, 2025)")

        // Create 50+ users with diverse roles and backgrounds
        val users = mutableListOf<User>()
        val profiles = mutableListOf<Profile>()

        // 2022 Alumni (15 users) - All are now alumni since cohort is finished
        val alumni2022 = createAlumniUsers(15, savedCohort2022, "alumna2022")
        users.addAll(alumni2022.map { it.first })
        profiles.addAll(alumni2022.map { it.second })

        // 2023 Alumni (12 users) - All are now alumni since cohort is finished
        val alumni2023 = createAlumniUsers(12, savedCohort2023, "alumna2023")
        users.addAll(alumni2023.map { it.first })
        profiles.addAll(alumni2023.map { it.second })

        // 2024 Alumni (15 users) - All are now alumni since cohort is finished
        val alumni2024 = createAlumniUsers(15, savedCohort2024, "alumna2024")
        users.addAll(alumni2024.map { it.first })
        profiles.addAll(alumni2024.map { it.second })

        // 2025 Participants (10 users) - Only active cohort has participants
        val participants2025 = createParticipantUsers(10, savedCohort2025, "participant2025")
        users.addAll(participants2025.map { it.first })
        profiles.addAll(participants2025.map { it.second })

        // Mentors (8 users)
        val mentors = createMentorUsers(8)
        users.addAll(mentors.map { it.first })
        profiles.addAll(mentors.map { it.second })

        // Save all users
        val savedUsers = users.map { userRepository.save(it) }
        println("✅ Created ${savedUsers.size} users")

        // Save all profiles
        profiles.forEach { profileRepository.save(it) }
        println("✅ Created ${profiles.size} profiles")

        // Create comprehensive events
        val events = createComprehensiveEvents()
        val savedEvents = events.map { eventRepository.save(it) }
        println("✅ Created ${savedEvents.size} events")

        // Create comprehensive resources
        val resources = createComprehensiveResources()
        val savedResources = resources.map { resourceRepository.save(it) }
        println("✅ Created ${savedResources.size} resources")

        // Create user events (RSVPs)
        val userEvents = createUserEvents(savedUsers, savedEvents)
        userEvents.forEach { userEventRepository.save(it) }
        println("✅ Created ${userEvents.size} user events (RSVPs)")

        // Create messages
        val messages = createComprehensiveMessages(savedUsers)
        messages.forEach { messageRepository.save(it) }
        println("✅ Created ${messages.size} messages")

        // Create connections
        val connections = createComprehensiveConnections(savedUsers)
        connections.forEach { connectionRepository.save(it) }
        println("✅ Created ${connections.size} connections")

        println("🎉 Data loading completed successfully!")
        println("📊 Summary:")
        println("   • Users: ${savedUsers.size}")
        println("   • Cohorts: 4")
        println("   • Events: ${savedEvents.size}")
        println("   • Resources: ${savedResources.size}")
        println("   • Messages: ${messages.size}")
        println("   • Connections: ${connections.size}")
        println("   • User Events: ${userEvents.size}")
        
        // Print sample login credentials
        println("🔑 Sample Login Credentials:")
        println("   • Participant 2025: participant2025_1@example.com / password123")
        println("   • Alumna 2024: alumna2024_1@example.com / password123")
        println("   • Alumna 2023: alumna2023_1@example.com / password123")
        println("   • Alumna 2022: alumna2022_1@example.com / password123")
        println("   • Mentor: mentor_1@example.com / password123")
        
        // Print cohort distribution
        println("📈 Cohort Distribution:")
        println("   • RISE 2022 (Completed): 15 Alumni")
        println("   • RISE 2023 (Completed): 12 Alumni")
        println("   • RISE 2024 (Completed): 15 Alumni")
        println("   • RISE 2025 (Active): 10 Participants")
        println("   • Mentors: 8 Users")
    }

    private fun createAlumniUsers(count: Int, cohort: Cohort, prefix: String): List<Pair<User, Profile>> {
        val users = mutableListOf<Pair<User, Profile>>()
        val names = listOf(
            "Nour Al-Rashid", "Amina Khoury", "Layla Al-Sabah", "Fatima Al-Zahra", "Mariam Hassan",
            "Zahra Al-Mansour", "Aisha Al-Khalifa", "Huda Al-Salem", "Rania Al-Mubarak", "Yasmin Al-Sabah",
            "Dalia Al-Kuwaiti", "Nada Al-Rashid", "Lina Al-Mansour", "Rima Al-Khalifa", "Sara Al-Salem",
            "Mona Al-Sabah", "Rana Al-Khalifa", "Lara Al-Mansour", "Salma Al-Salem", "Noura Al-Mubarak"
        )
        val positions = listOf(
            "VP of Engineering", "Chief Marketing Officer", "Senior Product Manager", "Director of Operations",
            "Head of Strategy", "VP of Sales", "Chief Technology Officer", "Director of Finance",
            "VP of Human Resources", "Chief Innovation Officer", "Senior Director", "VP of Customer Success",
            "Director of Marketing", "Head of Product", "VP of Business Development", "Chief Operating Officer",
            "Director of Strategy", "VP of Technology", "Head of Operations", "Senior VP"
        )
        val companies = listOf(
            "Future Tech", "Global Enterprises", "Innovation Labs", "Tech Solutions", "Digital Dynamics",
            "Strategic Partners", "Growth Ventures", "Elite Consulting", "NextGen Solutions", "Visionary Corp",
            "Excellence Group", "Pioneer Tech", "Leadership Institute", "Success Partners", "Innovation Hub",
            "Dynamic Solutions", "Strategic Ventures", "Elite Partners", "Future Dynamics", "Global Tech"
        )

        for (i in 1..count) {
            val user = User(
                email = "${prefix}_${i}@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                role = UserRole.ALUMNA, // All users from finished cohorts are alumni
                cohortId = cohort.id,
                lastLoginAt = LocalDateTime.now().minusDays((1..30).random().toLong())
            )
            
            val profile = Profile(
                userId = user.id,
                name = names[i - 1],
                position = positions[i - 1],
                company = companies[i - 1],
                skills = generateSkills(),
                bio = generateBio(names[i - 1], "alumna"),
                imageUrl = "https://example.com/${prefix}_${i}.jpg",
                linkedinUrl = "https://linkedin.com/in/${prefix}_${i}"
            )
            
            users.add(Pair(user, profile))
        }
        return users
    }

    private fun createParticipantUsers(count: Int, cohort: Cohort, prefix: String): List<Pair<User, Profile>> {
        val users = mutableListOf<Pair<User, Profile>>()
        val names = listOf(
            "Fatima Al-Zahra", "Layla Hassan", "Mariam Al-Rashid", "Aisha Al-Mansour", "Zahra Al-Khalifa",
            "Huda Al-Salem", "Rania Al-Mubarak", "Yasmin Al-Sabah", "Dalia Al-Kuwaiti", "Nada Al-Rashid"
        )
        val positions = listOf(
            "Senior Software Engineer", "Product Manager", "Business Analyst", "Data Scientist", "UX Designer",
            "Marketing Manager", "Sales Manager", "Project Manager", "Financial Analyst", "HR Specialist"
        )
        val companies = listOf(
            "Innovation Labs", "Tech Solutions", "Finance Corp", "Digital Dynamics", "Creative Agency",
            "Growth Ventures", "Elite Consulting", "NextGen Solutions", "Visionary Corp", "Excellence Group"
        )

        for (i in 1..count) {
            val user = User(
                email = "${prefix}_${i}@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                role = UserRole.PARTICIPANT, // Only 2025 cohort has participants
                cohortId = cohort.id,
                lastLoginAt = LocalDateTime.now().minusDays((1..7).random().toLong())
            )
            
            val profile = Profile(
                userId = user.id,
                name = names[i - 1],
                position = positions[i - 1],
                company = companies[i - 1],
                skills = generateSkills(),
                bio = generateBio(names[i - 1], "participant"),
                imageUrl = "https://example.com/${prefix}_${i}.jpg",
                linkedinUrl = "https://linkedin.com/in/${prefix}_${i}"
            )
            
            users.add(Pair(user, profile))
        }
        return users
    }

    private fun createMentorUsers(count: Int): List<Pair<User, Profile>> {
        val users = mutableListOf<Pair<User, Profile>>()
        val names = listOf(
            "Dr. Rania Mansour", "Dr. Layla Al-Sabah", "Dr. Fatima Al-Khalifa", "Dr. Mariam Al-Mansour",
            "Dr. Aisha Al-Rashid", "Dr. Zahra Al-Salem", "Dr. Huda Al-Mubarak", "Dr. Yasmin Al-Kuwaiti"
        )
        val positions = listOf(
            "CEO", "Executive Coach", "Leadership Consultant", "Strategic Advisor", "Business Mentor",
            "Career Coach", "Executive Director", "Principal Consultant"
        )
        val companies = listOf(
            "Leadership Consultancy", "Executive Coaching Group", "Strategic Advisory", "Mentorship Institute",
            "Career Development Center", "Leadership Academy", "Executive Mentoring", "Strategic Partners"
        )

        for (i in 1..count) {
            val user = User(
                email = "mentor_${i}@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                role = UserRole.MENTOR,
                lastLoginAt = LocalDateTime.now().minusDays((1..14).random().toLong())
            )
            
            val profile = Profile(
                userId = user.id,
                name = names[i - 1],
                position = positions[i - 1],
                company = companies[i - 1],
                skills = generateMentorSkills(),
                bio = generateBio(names[i - 1], "mentor"),
                imageUrl = "https://example.com/mentor_${i}.jpg",
                linkedinUrl = "https://linkedin.com/in/mentor_${i}"
            )
            
            users.add(Pair(user, profile))
        }
        return users
    }

    private fun generateSkills(): List<String> {
        val allSkills = listOf(
            "Leadership", "Strategic Planning", "Project Management", "Team Management", "Communication",
            "Problem Solving", "Data Analysis", "Business Strategy", "Marketing", "Sales",
            "Product Management", "Software Development", "UX/UI Design", "Financial Analysis", "Operations",
            "Human Resources", "Customer Success", "Business Development", "Innovation", "Digital Transformation",
            "Agile Development", "Scrum", "Lean Six Sigma", "Change Management", "Risk Management",
            "Stakeholder Management", "Negotiation", "Public Speaking", "Mentoring", "Coaching"
        )
        return allSkills.shuffled().take((3..6).random())
    }

    private fun generateMentorSkills(): List<String> {
        val mentorSkills = listOf(
            "Executive Coaching", "Leadership Development", "Strategic Consulting", "Career Development",
            "Business Strategy", "Change Management", "Organizational Development", "Performance Management",
            "Succession Planning", "Talent Development", "Executive Mentoring", "Board Advisory",
            "Crisis Management", "Stakeholder Relations", "Corporate Governance"
        )
        return mentorSkills.shuffled().take((4..7).random())
    }

    private fun generateBio(name: String, role: String): String {
        val bios = when (role) {
            "alumna" -> listOf(
                "RISE alumna passionate about empowering women in leadership roles.",
                "Experienced professional with a strong track record in strategic leadership.",
                "Dedicated to mentoring the next generation of women leaders.",
                "Successfully transitioned from technical to leadership roles.",
                "Committed to driving organizational change and innovation."
            )
            "participant" -> listOf(
                "Current RISE participant focused on developing leadership skills.",
                "Eager to learn and grow through the RISE program experience.",
                "Passionate about making a positive impact in my organization.",
                "Building a strong foundation for future leadership opportunities.",
                "Committed to continuous learning and professional development."
            )
            "mentor" -> listOf(
                "Experienced executive with 20+ years of leadership experience.",
                "Dedicated to developing the next generation of women leaders.",
                "Passionate about mentoring and coaching emerging talent.",
                "Strategic advisor with expertise in organizational transformation.",
                "Committed to advancing women in leadership positions."
            )
            else -> listOf("Professional with diverse experience in leadership and management.")
        }
        return bios.random()
    }

    private fun createComprehensiveEvents(): List<Event> {
        return listOf(
            // Public Events
            Event(
                title = "RISE Information Session 2025",
                description = "Join us for an information session about the RISE program for 2025 cohort.",
                date = LocalDateTime.now().plusDays(7),
                location = "NBK Head Office",
                isPublic = true
            ),
            Event(
                title = "Women in Leadership Conference 2025",
                description = "Annual conference celebrating women leaders across industries in Kuwait.",
                date = LocalDateTime.now().plusDays(14),
                location = "Kuwait Convention Center",
                isPublic = true
            ),
            Event(
                title = "Networking Mixer - Spring 2025",
                description = "Open networking event for women professionals in Kuwait.",
                date = LocalDateTime.now().plusDays(21),
                location = "Four Seasons Hotel",
                isPublic = true
            ),
            Event(
                title = "Tech Women Summit",
                description = "Celebrating women in technology and innovation.",
                date = LocalDateTime.now().plusDays(28),
                location = "Kuwait Digital Hub",
                isPublic = true
            ),
            Event(
                title = "Leadership Excellence Forum",
                description = "Forum focused on developing leadership excellence in women.",
                date = LocalDateTime.now().plusDays(35),
                location = "Kuwait Chamber of Commerce",
                isPublic = true
            ),
            Event(
                title = "Career Development Workshop",
                description = "Workshop on career planning and development strategies.",
                date = LocalDateTime.now().plusDays(42),
                location = "INSEAD Campus",
                isPublic = true
            ),
            
            // Private Events - Updated to reflect only 2025 participants
            Event(
                title = "RISE 2025 Cohort Workshop: Leadership Fundamentals",
                description = "Leadership fundamentals workshop for current RISE 2025 participants.",
                date = LocalDateTime.now().plusDays(10),
                location = "INSEAD Campus",
                isPublic = false
            ),
            Event(
                title = "RISE 2025 Cohort Workshop: Strategic Thinking",
                description = "Strategic thinking workshop for RISE 2025 participants.",
                date = LocalDateTime.now().plusDays(17),
                location = "INSEAD Campus",
                isPublic = false
            ),
            Event(
                title = "Alumni Mentoring Circle - Q1 2025",
                description = "Exclusive mentoring session for RISE alumni from all cohorts.",
                date = LocalDateTime.now().plusDays(24),
                location = "Private Venue",
                isPublic = false
            ),
            Event(
                title = "Mentor-Mentee Networking",
                description = "Networking session between mentors and current participants.",
                date = LocalDateTime.now().plusDays(31),
                location = "Private Venue",
                isPublic = false
            ),
            Event(
                title = "Alumni Success Stories Panel",
                description = "Panel discussion featuring successful RISE alumni.",
                date = LocalDateTime.now().plusDays(38),
                location = "INSEAD Campus",
                isPublic = false
            ),
            Event(
                title = "Executive Presence Workshop",
                description = "Workshop on developing executive presence for current participants.",
                date = LocalDateTime.now().plusDays(45),
                location = "INSEAD Campus",
                isPublic = false
            )
        )
    }

    private fun createComprehensiveResources(): List<Resource> {
        return listOf(
            // Leadership Resources
            Resource(
                title = "Leadership Fundamentals Guide",
                description = "Comprehensive guide to leadership fundamentals and best practices.",
                type = ResourceType.PDF,
                url = "https://example.com/resources/leadership-fundamentals.pdf",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Leadership Development"
            ),
            Resource(
                title = "Strategic Thinking Workshop",
                description = "Video workshop on developing strategic thinking skills.",
                type = ResourceType.VIDEO,
                url = "https://example.com/resources/strategic-thinking.mp4",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Strategic Planning"
            ),
            Resource(
                title = "Executive Communication Skills",
                description = "Guide to effective executive communication and presentation skills.",
                type = ResourceType.PDF,
                url = "https://example.com/resources/executive-communication.pdf",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Communication"
            ),
            Resource(
                title = "Change Management Strategies",
                description = "Comprehensive strategies for managing organizational change.",
                type = ResourceType.LINK,
                url = "https://example.com/resources/change-management",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Change Management"
            ),
            Resource(
                title = "Mentoring Best Practices",
                description = "Best practices for effective mentoring relationships.",
                type = ResourceType.PDF,
                url = "https://example.com/resources/mentoring-best-practices.pdf",
                targetRoles = listOf(UserRole.MENTOR, UserRole.ALUMNA),
                module = "Mentoring"
            ),
            Resource(
                title = "Career Development Roadmap",
                description = "Step-by-step roadmap for career development and advancement.",
                type = ResourceType.PDF,
                url = "https://example.com/resources/career-roadmap.pdf",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Career Development"
            ),
            Resource(
                title = "Networking Strategies",
                description = "Effective networking strategies for professional growth.",
                type = ResourceType.VIDEO,
                url = "https://example.com/resources/networking-strategies.mp4",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Networking"
            ),
            Resource(
                title = "Executive Presence Masterclass",
                description = "Masterclass on developing executive presence and leadership aura.",
                type = ResourceType.VIDEO,
                url = "https://example.com/resources/executive-presence.mp4",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Executive Presence"
            ),
            Resource(
                title = "Women in Leadership Research",
                description = "Latest research on women in leadership positions.",
                type = ResourceType.LINK,
                url = "https://example.com/resources/women-leadership-research",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA, UserRole.MENTOR),
                module = "Research"
            ),
            Resource(
                title = "RISE Program Handbook",
                description = "Complete handbook for RISE program participants.",
                type = ResourceType.PDF,
                url = "https://example.com/resources/rise-handbook.pdf",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Program Guide"
            )
        )
    }

    private fun createUserEvents(users: List<User>, events: List<Event>): List<UserEvent> {
        val userEvents = mutableListOf<UserEvent>()
        
        // Create realistic RSVPs
        events.forEach { event ->
            val eligibleUsers = if (event.isPublic) {
                users // All users can attend public events
            } else {
                users.filter { user ->
                    // Private events are for specific roles/cohorts
                    when {
                        event.title.contains("2025") -> user.role == UserRole.PARTICIPANT || user.role == UserRole.MENTOR
                        event.title.contains("Alumni") -> user.role == UserRole.ALUMNA || user.role == UserRole.MENTOR
                        event.title.contains("Mentor") -> user.role == UserRole.MENTOR || user.role == UserRole.PARTICIPANT
                        else -> true
                    }
                }
            }
            
            // Random RSVPs for eligible users
            eligibleUsers.shuffled().take((eligibleUsers.size * 0.7).toInt()).forEach { user ->
                val rsvpStatus = listOf(RsvpStatus.GOING, RsvpStatus.INTERESTED, RsvpStatus.NOT_GOING).random()
                userEvents.add(
                    UserEvent(
                        user = user,
                        event = event,
                        rsvpStatus = rsvpStatus
                    )
                )
            }
        }
        
        return userEvents
    }

    private fun createComprehensiveMessages(users: List<User>): List<Message> {
        val messages = mutableListOf<Message>()
        
        // Create conversations between users
        users.chunked(2).forEach { userPair ->
            if (userPair.size == 2) {
                val sender = userPair[0]
                val receiver = userPair[1]
                
                // Create 3-5 messages per conversation
                repeat((3..5).random()) { messageIndex ->
                    messages.add(
                        Message(
                            senderId = if (messageIndex % 2 == 0) sender.id else receiver.id,
                            receiverId = if (messageIndex % 2 == 0) receiver.id else sender.id,
                            content = generateMessageContent(messageIndex),
                            sentAt = LocalDateTime.now().minusDays((1..30).random().toLong()),
                            isRead = (0..1).random() == 1
                        )
                    )
                }
            }
        }
        
        return messages
    }

    private fun generateMessageContent(messageIndex: Int): String {
        val messageTemplates = listOf(
            "Hi! How are you doing with the RISE program?",
            "I'd love to connect and discuss our experiences.",
            "Have you attended any recent workshops?",
            "The networking event was amazing! Did you enjoy it?",
            "I'm looking forward to our next mentoring session.",
            "How's your project coming along?",
            "Let's catch up soon!",
            "Thanks for the great advice in our last meeting.",
            "I'm excited about the upcoming leadership workshop.",
            "How can I help you with your career goals?"
        )
        return messageTemplates[messageIndex % messageTemplates.size]
    }

    private fun createComprehensiveConnections(users: List<User>): List<Connection> {
        val connections = mutableListOf<Connection>()
        
        // Create mentorship connections
        val mentors = users.filter { it.role == UserRole.MENTOR }
        val participants = users.filter { it.role == UserRole.PARTICIPANT } // Only 2025 participants
        val alumni = users.filter { it.role == UserRole.ALUMNA } // All alumni from 2022, 2023, 2024
        
        // Mentor-Participant connections (mentors with current 2025 participants)
        participants.forEach { participant ->
            val mentor = mentors.random()
            connections.add(
                Connection(
                    requesterId = participant.id,
                    targetId = mentor.id,
                    type = ConnectionType.MENTORSHIP,
                    status = ConnectionStatus.ACCEPTED,
                    requestedAt = LocalDateTime.now().minusDays((1..60).random().toLong()),
                    respondedAt = LocalDateTime.now().minusDays((1..30).random().toLong())
                )
            )
        }
        
        // Alumni-Participant connections (alumni mentoring current participants)
        participants.take(participants.size / 2).forEach { participant ->
            val alumna = alumni.random()
            connections.add(
                Connection(
                    requesterId = participant.id,
                    targetId = alumna.id,
                    type = ConnectionType.CONNECT,
                    status = ConnectionStatus.ACCEPTED,
                    requestedAt = LocalDateTime.now().minusDays((1..45).random().toLong()),
                    respondedAt = LocalDateTime.now().minusDays((1..20).random().toLong())
                )
            )
        }
        
        // Alumni-Alumni connections (networking between alumni)
        alumni.take(10).forEach { alumna1 ->
            val alumna2 = alumni.filter { it.id != alumna1.id }.random()
            connections.add(
                Connection(
                    requesterId = alumna1.id,
                    targetId = alumna2.id,
                    type = ConnectionType.CONNECT,
                    status = ConnectionStatus.ACCEPTED,
                    requestedAt = LocalDateTime.now().minusDays((1..90).random().toLong()),
                    respondedAt = LocalDateTime.now().minusDays((1..60).random().toLong())
                )
            )
        }
        
        // Pending connections (some current participants seeking mentorship/connections)
        participants.take(3).forEach { participant ->
            val target = (mentors + alumni).random()
            connections.add(
                Connection(
                    requesterId = participant.id,
                    targetId = target.id,
                    type = if (target.role == UserRole.MENTOR) ConnectionType.MENTORSHIP else ConnectionType.CONNECT,
                    status = ConnectionStatus.PENDING,
                    requestedAt = LocalDateTime.now().minusDays((1..7).random().toLong())
                )
            )
        }
        
        return connections
    }
} 