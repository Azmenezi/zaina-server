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
        // Create cohorts
        val cohort2024 = Cohort(
            name = "RISE 2024",
            startDate = LocalDate.of(2024, 1, 15),
            endDate = LocalDate.of(2024, 12, 15)
        )
        
        val cohort2023 = Cohort(
            name = "RISE 2023",
            startDate = LocalDate.of(2023, 1, 15),
            endDate = LocalDate.of(2023, 12, 15)
        )
        
        val savedCohort2024 = cohortRepository.save(cohort2024)
        val savedCohort2023 = cohortRepository.save(cohort2023)

        // Create users - 3 participants, 2 alumni, 1 mentor
        val participant1 = User(
            email = "participant1@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.PARTICIPANT,
            cohortId = savedCohort2024.id
        )
        
        val participant2 = User(
            email = "participant2@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.PARTICIPANT,
            cohortId = savedCohort2024.id
        )
        
        val participant3 = User(
            email = "participant3@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.PARTICIPANT,
            cohortId = savedCohort2024.id
        )
        
        val alumna1 = User(
            email = "alumna1@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.ALUMNA,
            cohortId = savedCohort2023.id
        )
        
        val alumna2 = User(
            email = "alumna2@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.ALUMNA,
            cohortId = savedCohort2023.id
        )
        
        val mentor = User(
            email = "mentor@example.com",
            passwordHash = passwordEncoder.encode("password123"),
            role = UserRole.MENTOR
        )

        val savedParticipant1 = userRepository.save(participant1)
        val savedParticipant2 = userRepository.save(participant2)
        val savedParticipant3 = userRepository.save(participant3)
        val savedAlumna1 = userRepository.save(alumna1)
        val savedAlumna2 = userRepository.save(alumna2)
        val savedMentor = userRepository.save(mentor)

        // Create profiles
        val profiles = listOf(
            Profile(
                userId = savedParticipant1.id,
                name = "Fatima Al-Zahra",
                position = "Senior Software Engineer",
                company = "Innovation Labs",
                skills = listOf("Software Engineering", "Team Leadership", "Agile Development"),
                bio = "Current RISE participant focusing on technical leadership.",
                imageUrl = "https://example.com/fatima.jpg",
                linkedinUrl = "https://linkedin.com/in/fatima-alzahra"
            ),
            Profile(
                userId = savedParticipant2.id,
                name = "Layla Hassan",
                position = "Product Manager",
                company = "Tech Solutions",
                skills = listOf("Product Management", "Strategy", "User Experience"),
                bio = "Passionate about creating user-centered products that make a difference.",
                imageUrl = "https://example.com/layla.jpg",
                linkedinUrl = "https://linkedin.com/in/layla-hassan"
            ),
            Profile(
                userId = savedParticipant3.id,
                name = "Mariam Al-Rashid",
                position = "Business Analyst",
                company = "Finance Corp",
                skills = listOf("Business Analysis", "Data Analytics", "Process Improvement"),
                bio = "Data-driven professional with a passion for process optimization.",
                imageUrl = "https://example.com/mariam.jpg",
                linkedinUrl = null
            ),
            Profile(
                userId = savedAlumna1.id,
                name = "Nour Al-Rashid",
                position = "VP of Engineering",
                company = "Future Tech",
                skills = listOf("Executive Leadership", "Strategic Planning", "Mentoring"),
                bio = "RISE alumna and mentor, passionate about empowering women in tech.",
                imageUrl = "https://example.com/nour.jpg",
                linkedinUrl = "https://linkedin.com/in/nour-alrashid"
            ),
            Profile(
                userId = savedAlumna2.id,
                name = "Amina Khoury",
                position = "Chief Marketing Officer",
                company = "Global Enterprises",
                skills = listOf("Marketing Strategy", "Brand Management", "Digital Transformation"),
                bio = "Marketing executive focused on driving digital transformation and brand growth.",
                imageUrl = "https://example.com/amina.jpg",
                linkedinUrl = "https://linkedin.com/in/amina-khoury"
            ),
            Profile(
                userId = savedMentor.id,
                name = "Dr. Rania Mansour",
                position = "CEO",
                company = "Leadership Consultancy",
                skills = listOf("Executive Coaching", "Leadership Development", "Strategic Consulting"),
                bio = "Experienced CEO and leadership consultant with 20+ years of experience in developing women leaders.",
                imageUrl = "https://example.com/rania.jpg",
                linkedinUrl = "https://linkedin.com/in/dr-rania-mansour"
            )
        )

        profiles.forEach { profileRepository.save(it) }

        // Create events - 3 public, 2 private
        val events = listOf(
            Event(
                title = "RISE Information Session",
                description = "Join us for an information session about the RISE program.",
                date = LocalDateTime.now().plusDays(7),
                location = "NBK Head Office",
                isPublic = true
            ),
            Event(
                title = "Women in Leadership Conference",
                description = "Annual conference celebrating women leaders across industries.",
                date = LocalDateTime.now().plusDays(14),
                location = "Kuwait Convention Center",
                isPublic = true
            ),
            Event(
                title = "Networking Mixer",
                description = "Open networking event for women professionals.",
                date = LocalDateTime.now().plusDays(21),
                location = "Four Seasons Hotel",
                isPublic = true
            ),
            Event(
                title = "Participant Workshop: Leadership Skills",
                description = "Interactive workshop for current participants on leadership development.",
                date = LocalDateTime.now().plusDays(10),
                location = "INSEAD Campus",
                isPublic = false
            ),
            Event(
                title = "Alumni Mentoring Circle",
                description = "Exclusive mentoring session for RISE alumni.",
                date = LocalDateTime.now().plusDays(28),
                location = "Private Venue",
                isPublic = false
            )
        )

        val savedEvents = events.map { eventRepository.save(it) }

        // Create UserEvents (RSVPs) for more realistic event data
        val userEvents = listOf(
            // Participant 1 RSVPs
            UserEvent(
                user = savedParticipant1,
                event = savedEvents[0], // RISE Info Session
                rsvpStatus = RsvpStatus.GOING
            ),
            UserEvent(
                user = savedParticipant1,
                event = savedEvents[3], // Leadership Workshop
                rsvpStatus = RsvpStatus.GOING
            ),
            // Participant 2 RSVPs
            UserEvent(
                user = savedParticipant2,
                event = savedEvents[1], // Leadership Conference
                rsvpStatus = RsvpStatus.INTERESTED
            ),
            UserEvent(
                user = savedParticipant2,
                event = savedEvents[3], // Leadership Workshop
                rsvpStatus = RsvpStatus.GOING
            ),
            // Participant 3 RSVPs
            UserEvent(
                user = savedParticipant3,
                event = savedEvents[2], // Networking Mixer
                rsvpStatus = RsvpStatus.GOING
            ),
            // Alumna 1 RSVPs
            UserEvent(
                user = savedAlumna1,
                event = savedEvents[1], // Leadership Conference
                rsvpStatus = RsvpStatus.GOING
            ),
            UserEvent(
                user = savedAlumna1,
                event = savedEvents[4], // Alumni Mentoring Circle
                rsvpStatus = RsvpStatus.GOING
            ),
            // Alumna 2 RSVPs
            UserEvent(
                user = savedAlumna2,
                event = savedEvents[2], // Networking Mixer
                rsvpStatus = RsvpStatus.GOING
            ),
            UserEvent(
                user = savedAlumna2,
                event = savedEvents[4], // Alumni Mentoring Circle
                rsvpStatus = RsvpStatus.GOING
            ),
            // Mentor RSVPs
            UserEvent(
                user = savedMentor,
                event = savedEvents[1], // Leadership Conference
                rsvpStatus = RsvpStatus.GOING
            ),
            UserEvent(
                user = savedMentor,
                event = savedEvents[4], // Alumni Mentoring Circle
                rsvpStatus = RsvpStatus.GOING
            )
        )

        userEvents.forEach { userEventRepository.save(it) }

        // Create 4 resources
        val resources = listOf(
            Resource(
                title = "Leadership Fundamentals",
                description = "Essential leadership skills for emerging leaders",
                type = ResourceType.PDF,
                url = "https://example.com/leadership-fundamentals.pdf",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Leadership Development"
            ),
            Resource(
                title = "Networking Strategies",
                description = "Effective networking techniques for professional growth",
                type = ResourceType.VIDEO,
                url = "https://example.com/networking-strategies-video",
                targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
                module = "Professional Development"
            ),
            Resource(
                title = "Executive Coaching Guide",
                description = "Advanced coaching techniques for senior leaders",
                type = ResourceType.PDF,
                url = "https://example.com/executive-coaching.pdf",
                targetRoles = listOf(UserRole.ALUMNA, UserRole.MENTOR),
                module = "Executive Development"
            ),
            Resource(
                title = "Mentorship Best Practices",
                description = "Guide for effective mentoring relationships",
                type = ResourceType.LINK,
                url = "https://example.com/mentorship-guide",
                targetRoles = listOf(UserRole.MENTOR, UserRole.ALUMNA),
                module = "Mentorship"
            )
        )

        resources.forEach { resourceRepository.save(it) }

        // Create comprehensive message conversations
        val messages = listOf(
            // Conversation 1: Participant 1 and Mentor
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedMentor.id,
                content = "Hi Dr. Mansour, I would love to learn more about your journey to becoming a CEO. Could we schedule a mentoring session?",
                sentAt = LocalDateTime.now().minusDays(3),
                isRead = true
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedParticipant1.id,
                content = "Hello Fatima! I'd be delighted to share my experience with you. Let's schedule a call next week. What days work best for you?",
                sentAt = LocalDateTime.now().minusDays(2),
                isRead = true
            ),
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedMentor.id,
                content = "Thank you so much! I'm available Tuesday or Wednesday afternoon. Would either of those work for you?",
                sentAt = LocalDateTime.now().minusDays(1),
                isRead = false
            ),
            
            // Conversation 2: Participant 2 and Alumna 1
            Message(
                senderId = savedParticipant2.id,
                receiverId = savedAlumna1.id,
                content = "Hi Nour! I saw your profile and I'm really interested in transitioning to engineering leadership. Would you be open to sharing some advice?",
                sentAt = LocalDateTime.now().minusHours(12),
                isRead = true
            ),
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedParticipant2.id,
                content = "Hi Layla! Of course, I'd be happy to help. The transition from IC to leadership is challenging but rewarding. Are you thinking about moving into people management or technical leadership?",
                sentAt = LocalDateTime.now().minusHours(8),
                isRead = false
            ),
            
            // Conversation 3: Alumna 2 and Mentor
            Message(
                senderId = savedAlumna2.id,
                receiverId = savedMentor.id,
                content = "Dr. Mansour, I'm considering a pivot to a more strategic role. Would you be able to provide some guidance on executive positioning?",
                sentAt = LocalDateTime.now().minusHours(6),
                isRead = false
            ),
            
            // Group conversation simulation: Participant 3 reaching out
            Message(
                senderId = savedParticipant3.id,
                receiverId = savedAlumna2.id,
                content = "Hi Amina! I loved your presentation at the last RISE event about digital transformation. I work in business analysis and would love to learn more about your career path.",
                sentAt = LocalDateTime.now().minusHours(3),
                isRead = false
            )
        )

        messages.forEach { messageRepository.save(it) }

        // Create diverse connection requests
        val connections = listOf(
            // Pending mentorship request (Participant 2 → Mentor)
            Connection(
                requesterId = savedParticipant2.id,
                targetId = savedMentor.id,
                type = ConnectionType.MENTORSHIP,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(6)
            ),
            
            // Accepted connection (Participant 1 → Alumna 1)
            Connection(
                requesterId = savedParticipant1.id,
                targetId = savedAlumna1.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(5),
                respondedAt = LocalDateTime.now().minusDays(4)
            ),
            
            // Accepted mentorship (Participant 1 → Mentor)
            Connection(
                requesterId = savedParticipant1.id,
                targetId = savedMentor.id,
                type = ConnectionType.MENTORSHIP,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(7),
                respondedAt = LocalDateTime.now().minusDays(6)
            ),
            
            // Pending connection (Participant 3 → Alumna 2)
            Connection(
                requesterId = savedParticipant3.id,
                targetId = savedAlumna2.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(3)
            ),
            
            // Declined connection example
            Connection(
                requesterId = savedParticipant2.id,
                targetId = savedAlumna1.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.DECLINED,
                requestedAt = LocalDateTime.now().minusDays(10),
                respondedAt = LocalDateTime.now().minusDays(9)
            ),
            
            // Alumni connecting with each other
            Connection(
                requesterId = savedAlumna1.id,
                targetId = savedAlumna2.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(15),
                respondedAt = LocalDateTime.now().minusDays(14)
            )
        )

        connections.forEach { connectionRepository.save(it) }

        println("==================================================")
        println("🚀 COMPREHENSIVE MOCK DATA LOADED SUCCESSFULLY! 🚀")
        println("==================================================")
        println()
        println("📧 TEST ACCOUNTS:")
        println("   • Participant 1: participant1@example.com / password123")
        println("   • Participant 2: participant2@example.com / password123")
        println("   • Participant 3: participant3@example.com / password123")
        println("   • Alumna 1: alumna1@example.com / password123")
        println("   • Alumna 2: alumna2@example.com / password123")
        println("   • Mentor: mentor@example.com / password123")
        println()
        println("📊 DATA SUMMARY:")
        println("   • 📚 2 Cohorts (2023, 2024)")
        println("   • 👥 6 Users (3 participants, 2 alumni, 1 mentor)")
        println("   • 📝 6 Complete profiles with skills and LinkedIn URLs")
        println("   • 🎉 5 Events (3 public, 2 private)")
        println("   • 📅 11 Event RSVPs across all users")
        println("   • 📖 4 Resources with role-based access")
        println("   • 💬 7 Messages across multiple conversations")
        println("   • 🤝 6 Connections (pending, accepted, declined)")
        println()
        println("🔗 FEATURES TO TEST:")
        println("   • Profile search by name, company, skills")
        println("   • Messaging between users")
        println("   • Connection requests (regular + mentorship)")
        println("   • Event RSVPs and attendee lists")
        println("   • Role-based resource access")
        println("   • Pagination on events and resources")
        println()
        println("🌐 API ACCESS:")
        println("   • API: http://localhost:8080")
        println("   • Swagger UI: http://localhost:8080/swagger-ui.html")
        println("==================================================")
    }
} 