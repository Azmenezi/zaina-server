package com.zaina.zaina.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class RequestLoggingInterceptor : HandlerInterceptor {
    
    private val logger = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val startTime = System.currentTimeMillis()
        request.setAttribute("startTime", startTime)
        
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        logger.info("""
            
            ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
            ║ 🌐 HTTP REQUEST START - $timestamp
            ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
            ║ Method: ${request.method}
            ║ URI: ${request.requestURI}
            ║ Query String: ${request.queryString ?: "None"}
            ║ Remote Address: ${getClientIpAddress(request)}
            ║ User Agent: ${request.getHeader("User-Agent") ?: "Unknown"}
            ║ Content Type: ${request.contentType ?: "None"}
            ║ Content Length: ${request.contentLengthLong}
            ║ Authorization: ${if (request.getHeader("Authorization") != null) "Bearer ***[HIDDEN]***" else "None"}
            ║ Headers: ${getHeadersString(request)}
            ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
        """.trimIndent())
        
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute("startTime") as? Long ?: return
        val duration = System.currentTimeMillis() - startTime
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        val statusEmoji = when {
            response.status < 300 -> "✅"
            response.status < 400 -> "🔄"
            response.status < 500 -> "⚠️"
            else -> "❌"
        }
        
        logger.info("""
            
            ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
            ║ 🏁 HTTP REQUEST END - $timestamp
            ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
            ║ Method: ${request.method}
            ║ URI: ${request.requestURI}
            ║ Status: $statusEmoji ${response.status} (${getStatusMessage(response.status)})
            ║ Duration: ${duration}ms
            ║ Response Content Type: ${response.contentType ?: "None"}
            ║ Exception: ${ex?.message ?: "None"}
            ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
        """.trimIndent())
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (xForwardedFor != null && xForwardedFor.isNotEmpty()) {
            return xForwardedFor.split(",")[0].trim()
        }
        
        val xRealIp = request.getHeader("X-Real-IP")
        if (xRealIp != null && xRealIp.isNotEmpty()) {
            return xRealIp
        }
        
        return request.remoteAddr ?: "Unknown"
    }

    private fun getHeadersString(request: HttpServletRequest): String {
        return request.headerNames.asSequence()
            .map { headerName ->
                val value = if (headerName.equals("authorization", ignoreCase = true)) {
                    "***[HIDDEN]***"
                } else {
                    request.getHeader(headerName)
                }
                "$headerName: $value"
            }
            .joinToString(", ")
    }

    private fun getStatusMessage(status: Int): String {
        return when (status) {
            200 -> "OK"
            201 -> "CREATED"
            204 -> "NO CONTENT"
            400 -> "BAD REQUEST"
            401 -> "UNAUTHORIZED"
            403 -> "FORBIDDEN"
            404 -> "NOT FOUND"
            405 -> "METHOD NOT ALLOWED"
            409 -> "CONFLICT"
            422 -> "UNPROCESSABLE ENTITY"
            500 -> "INTERNAL SERVER ERROR"
            502 -> "BAD GATEWAY"
            503 -> "SERVICE UNAVAILABLE"
            else -> "HTTP $status"
        }
    }
} 