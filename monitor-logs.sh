#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 Zaina Application - Log Monitor${NC}"
echo "====================================="
echo ""

# Check if log file exists
LOG_FILE="logs/zaina-application.log"

if [ ! -f "$LOG_FILE" ]; then
    echo -e "${RED}❌ Log file not found: $LOG_FILE${NC}"
    echo -e "${YELLOW}💡 Make sure to start the application with: ./test-logging.sh${NC}"
    exit 1
fi

echo -e "${GREEN}📊 Log Monitoring Options:${NC}"
echo ""
echo "1. 🌐 HTTP Requests Only"
echo "2. 🔌 WebSocket Events Only"
echo "3. 💬 Chat Messages Only"
echo "4. 🔐 Authentication Events Only"
echo "5. ⚠️  Errors and Warnings Only"
echo "6. 📋 All Logs (Full Stream)"
echo "7. 🔍 Custom Filter"
echo ""

read -p "Select monitoring option (1-7): " choice

case $choice in
    1)
        echo -e "${CYAN}🌐 Monitoring HTTP Requests...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "HTTP REQUEST\|Duration:"
        ;;
    2)
        echo -e "${CYAN}🔌 Monitoring WebSocket Events...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "WEBSOCKET"
        ;;
    3)
        echo -e "${CYAN}💬 Monitoring Chat Messages...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "CHAT MESSAGE\|TYPING INDICATOR\|READ RECEIPT"
        ;;
    4)
        echo -e "${CYAN}🔐 Monitoring Authentication Events...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "AUTHENTICATION\|JWT\|LOGIN\|CONNECT ATTEMPT"
        ;;
    5)
        echo -e "${CYAN}⚠️ Monitoring Errors and Warnings...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "ERROR\|WARN\|FAILED\|❌\|⚠️"
        ;;
    6)
        echo -e "${CYAN}📋 Monitoring All Logs...${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE"
        ;;
    7)
        echo ""
        read -p "Enter custom filter pattern: " pattern
        echo -e "${CYAN}🔍 Monitoring logs with pattern: $pattern${NC}"
        echo "Press Ctrl+C to stop"
        echo ""
        tail -f "$LOG_FILE" | grep --line-buffered "$pattern"
        ;;
    *)
        echo -e "${RED}❌ Invalid option selected${NC}"
        exit 1
        ;;
esac 