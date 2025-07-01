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
            // Conversation 1: Participant 1 (Fatima) and Mentor (Dr. Rania) - Active mentorship
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedMentor.id,
                content = "Hi Dr. Mansour, I would love to learn more about your journey to becoming a CEO. Could we schedule a mentoring session?",
                sentAt = LocalDateTime.now().minusDays(7),
                isRead = true
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedParticipant1.id,
                content = "Hello Fatima! I'd be delighted to share my experience with you. Let's schedule a call next week. What days work best for you?",
                sentAt = LocalDateTime.now().minusDays(6),
                isRead = true
            ),
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedMentor.id,
                content = "Thank you so much! I'm available Tuesday or Wednesday afternoon. Would either of those work for you?",
                sentAt = LocalDateTime.now().minusDays(5),
                isRead = true
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedParticipant1.id,
                content = "Perfect! Let's do Wednesday at 2 PM. I'll send you a calendar invite. In the meantime, think about specific leadership challenges you're facing.",
                sentAt = LocalDateTime.now().minusDays(4),
                isRead = true
            ),
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedMentor.id,
                content = "Wonderful! I've been struggling with team communication and decision-making in ambiguous situations. Looking forward to our chat!",
                sentAt = LocalDateTime.now().minusDays(3),
                isRead = true
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedParticipant1.id,
                content = "Those are excellent topics to discuss. I'll prepare some frameworks that have worked well for me. See you Wednesday!",
                sentAt = LocalDateTime.now().minusDays(2),
                isRead = false
            ),
            
            // Conversation 2: Participant 2 (Layla) and Alumna 1 (Nour) - Career guidance
            Message(
                senderId = savedParticipant2.id,
                receiverId = savedAlumna1.id,
                content = "Hi Nour! I saw your profile and I'm really interested in transitioning to engineering leadership. Would you be open to sharing some advice?",
                sentAt = LocalDateTime.now().minusDays(3),
                isRead = true
            ),
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedParticipant2.id,
                content = "Hi Layla! Of course, I'd be happy to help. The transition from IC to leadership is challenging but rewarding. Are you thinking about people management or technical leadership?",
                sentAt = LocalDateTime.now().minusDays(2),
                isRead = true
            ),
            Message(
                senderId = savedParticipant2.id,
                receiverId = savedAlumna1.id,
                content = "I'm more interested in people management. I love mentoring junior team members and I think I could make a bigger impact as a leader.",
                sentAt = LocalDateTime.now().minusDays(1),
                isRead = true
            ),
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedParticipant2.id,
                content = "That's a great motivation! The key is developing your emotional intelligence and learning to influence without authority. Have you had any leadership opportunities yet?",
                sentAt = LocalDateTime.now().minusHours(18),
                isRead = true
            ),
            Message(
                senderId = savedParticipant2.id,
                receiverId = savedAlumna1.id,
                content = "I've been leading our product roadmap discussions and mentoring two new hires. It's been really fulfilling but also overwhelming at times.",
                sentAt = LocalDateTime.now().minusHours(12),
                isRead = false
            ),
            
            // Conversation 3: Alumna 2 (Amina) and Mentor (Dr. Rania) - Strategic career pivot
            Message(
                senderId = savedAlumna2.id,
                receiverId = savedMentor.id,
                content = "Dr. Mansour, I'm considering a pivot to a more strategic role, possibly consulting. Would you be able to provide some guidance on executive positioning?",
                sentAt = LocalDateTime.now().minusDays(1),
                isRead = true
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedAlumna2.id,
                content = "Absolutely, Amina! Your CMO background gives you a unique perspective for strategic consulting. What specific area of strategy interests you most?",
                sentAt = LocalDateTime.now().minusHours(20),
                isRead = true
            ),
            Message(
                senderId = savedAlumna2.id,
                receiverId = savedMentor.id,
                content = "I'm particularly interested in digital transformation and organizational change. Many companies struggle with modernizing their marketing and customer engagement strategies.",
                sentAt = LocalDateTime.now().minusHours(18),
                isRead = false
            ),
            
            // Conversation 4: Participant 3 (Mariam) and Alumna 2 (Amina) - Industry insights
            Message(
                senderId = savedParticipant3.id,
                receiverId = savedAlumna2.id,
                content = "Hi Amina! I loved your presentation at the last RISE event about digital transformation. I work in business analysis and would love to learn more about your career path.",
                sentAt = LocalDateTime.now().minusHours(15),
                isRead = true
            ),
            Message(
                senderId = savedAlumna2.id,
                receiverId = savedParticipant3.id,
                content = "Thank you, Mariam! I'm glad you found it helpful. Business analysis is such a valuable skill - it's actually where I started before moving into marketing strategy.",
                sentAt = LocalDateTime.now().minusHours(12),
                isRead = true
            ),
            Message(
                senderId = savedParticipant3.id,
                receiverId = savedAlumna2.id,
                content = "Really? That's encouraging! I sometimes feel like business analysis is too behind-the-scenes. How did you make the transition to a more strategic role?",
                sentAt = LocalDateTime.now().minusHours(10),
                isRead = false
            ),
            
            // Conversation 5: Alumni connecting - Nour and Amina
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedAlumna2.id,
                content = "Hey Amina! Hope you're doing well. I've been thinking about our conversation at the alumni meetup about cross-functional collaboration.",
                sentAt = LocalDateTime.now().minusDays(5),
                isRead = true
            ),
            Message(
                senderId = savedAlumna2.id,
                receiverId = savedAlumna1.id,
                content = "Hi Nour! Yes, that was such a great discussion. I've actually been implementing some of those ideas with my eng teams. The results have been promising!",
                sentAt = LocalDateTime.now().minusDays(4),
                isRead = true
            ),
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedAlumna2.id,
                content = "That's fantastic! I'd love to hear more details. Maybe we could do a quick call sometime this week to share best practices?",
                sentAt = LocalDateTime.now().minusDays(3),
                isRead = false
            ),
            
            // Recent messages for WebSocket testing - Very recent timestamps
            Message(
                senderId = savedParticipant1.id,
                receiverId = savedParticipant2.id,
                content = "Hey Layla! Are you attending the leadership workshop next week? Would be great to connect there!",
                sentAt = LocalDateTime.now().minusMinutes(30),
                isRead = false
            ),
            Message(
                senderId = savedParticipant2.id,
                receiverId = savedParticipant3.id,
                content = "Hi Mariam! Just saw your recent work on the process improvement project. Really impressive analysis!",
                sentAt = LocalDateTime.now().minusMinutes(15),
                isRead = false
            ),
            Message(
                senderId = savedParticipant3.id,
                receiverId = savedParticipant1.id,
                content = "Fatima, I'd love to learn more about your software engineering background. Are you available for a quick coffee chat this week?",
                sentAt = LocalDateTime.now().minusMinutes(10),
                isRead = false
            ),
            Message(
                senderId = savedAlumna1.id,
                receiverId = savedMentor.id,
                content = "Dr. Mansour, thank you for the mentorship session last week. The advice about executive presence was incredibly valuable.",
                sentAt = LocalDateTime.now().minusMinutes(5),
                isRead = false
            ),
            Message(
                senderId = savedMentor.id,
                receiverId = savedAlumna2.id,
                content = "Amina, I've been thinking about your consulting plans. I have a contact at McKinsey who might be worth connecting with. Let me know if you're interested!",
                sentAt = LocalDateTime.now().minusMinutes(2),
                isRead = false
            )
        )

        messages.forEach { messageRepository.save(it) }

        // Create diverse connection requests - Enhanced with more realistic scenarios
        val connections = listOf(
            // Accepted mentorship (Participant 1 → Mentor) - Established relationship
            Connection(
                requesterId = savedParticipant1.id,
                targetId = savedMentor.id,
                type = ConnectionType.MENTORSHIP,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(10),
                respondedAt = LocalDateTime.now().minusDays(9)
            ),
            
            // Accepted connection (Participant 1 → Alumna 1) - Professional networking
            Connection(
                requesterId = savedParticipant1.id,
                targetId = savedAlumna1.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(8),
                respondedAt = LocalDateTime.now().minusDays(7)
            ),
            
            // Alumni connecting with each other - Established network
            Connection(
                requesterId = savedAlumna1.id,
                targetId = savedAlumna2.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(20),
                respondedAt = LocalDateTime.now().minusDays(19)
            ),
            
            // Recent pending requests for testing
            Connection(
                requesterId = savedParticipant2.id,
                targetId = savedMentor.id,
                type = ConnectionType.MENTORSHIP,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(12)
            ),
            
            Connection(
                requesterId = savedParticipant3.id,
                targetId = savedAlumna2.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(8)
            ),
            
            Connection(
                requesterId = savedParticipant2.id,
                targetId = savedParticipant1.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(4)
            ),
            
            Connection(
                requesterId = savedParticipant3.id,
                targetId = savedParticipant2.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.PENDING,
                requestedAt = LocalDateTime.now().minusHours(2)
            ),
            
            // Example of declined connection for testing edge cases
            Connection(
                requesterId = savedParticipant2.id,
                targetId = savedAlumna1.id,
                type = ConnectionType.CONNECT,
                status = ConnectionStatus.DECLINED,
                requestedAt = LocalDateTime.now().minusDays(15),
                respondedAt = LocalDateTime.now().minusDays(14)
            ),
            
            // Cross-role connections for diversity
            Connection(
                requesterId = savedAlumna2.id,
                targetId = savedMentor.id,
                type = ConnectionType.MENTORSHIP,
                status = ConnectionStatus.ACCEPTED,
                requestedAt = LocalDateTime.now().minusDays(25),
                respondedAt = LocalDateTime.now().minusDays(24)
            )
        )

        connections.forEach { connectionRepository.save(it) }

        println("==================================================")
        println("🚀 COMPREHENSIVE MOCK DATA LOADED SUCCESSFULLY! 🚀")
        println("==================================================")
        println()
        println("📧 TEST ACCOUNTS:")
        println("   • Participant 1: participant1@example.com / password123")
        println("     └─ Fatima Al-Zahra (Senior Software Engineer)")
        println("   • Participant 2: participant2@example.com / password123")
        println("     └─ Layla Hassan (Product Manager)")
        println("   • Participant 3: participant3@example.com / password123")
        println("     └─ Mariam Al-Rashid (Business Analyst)")
        println("   • Alumna 1: alumna1@example.com / password123")
        println("     └─ Nour Al-Rashid (VP of Engineering)")
        println("   • Alumna 2: alumna2@example.com / password123")
        println("     └─ Amina Khoury (Chief Marketing Officer)")
        println("   • Mentor: mentor@example.com / password123")
        println("     └─ Dr. Rania Mansour (CEO)")
        println()
        println("📊 DATA SUMMARY:")
        println("   • 📚 2 Cohorts (2023, 2024)")
        println("   • 👥 6 Users (3 participants, 2 alumni, 1 mentor)")
        println("   • 📝 6 Complete profiles with skills and LinkedIn URLs")
        println("   • 🎉 5 Events (3 public, 2 private)")
        println("   • 📅 11 Event RSVPs across all users")
        println("   • 📖 4 Resources with role-based access")
        println("   • 💬 24 Messages in 6 active conversations")
        println("   • 🤝 9 Connections (4 pending, 4 accepted, 1 declined)")
        println()
        println("🔗 CHAT SYSTEM TEST SCENARIOS:")
        println("   • Active mentorship: Fatima ↔ Dr. Rania (6 messages)")
        println("   • Career guidance: Layla ↔ Nour (5 messages)")
        println("   • Strategic planning: Amina ↔ Dr. Rania (3 messages)")
        println("   • Industry insights: Mariam ↔ Amina (3 messages)")
        println("   • Alumni networking: Nour ↔ Amina (3 messages)")
        println("   • Recent messages for WebSocket testing (5 recent messages)")
        println()
        println("🤝 CONNECTION SYSTEM TEST SCENARIOS:")
        println("   • ✅ Established mentorships and connections")
        println("   • ⏳ Fresh pending requests (4 recent)")
        println("   • ❌ Declined connection example")
        println("   • 🔄 Cross-role relationship diversity")
        println()
        println("🧪 TESTING FEATURES:")
        println("   • Profile search by name, company, skills")
        println("   • Real-time messaging via WebSocket")
        println("   • Connection request workflows")
        println("   • Message read/unread status")
        println("   • Event RSVPs and attendee lists")
        println("   • Role-based resource access")
        println()
        println("🌐 ACCESS POINTS:")
        println("   • API: http://localhost:8080")
        println("   • Swagger UI: http://localhost:8080/swagger-ui.html")
        println("   • WebSocket Test: frontend-websocket-example.html")
        println("   • Logging: ./monitor-logs.sh")
        println("==================================================")
    }
} 