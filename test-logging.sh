#!/bin/bash

echo "🚀 Zaina Application - Comprehensive Logging Test"
echo "=================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info "Starting Zaina application with comprehensive logging..."
echo ""
print_info "📋 What will be logged:"
echo "   • All HTTP requests with full details (method, headers, timing)"
echo "   • WebSocket connections and disconnections"
echo "   • Real-time chat messages and broadcasts"
echo "   • User authentication events"
echo "   • Typing indicators and read receipts"
echo "   • Connection requests and responses"
echo "   • Database operations (SQL queries)"
echo ""

print_warning "This will start the application with DETAILED LOGGING enabled."
print_warning "Log output will be comprehensive and may impact performance."
echo ""

read -p "Do you want to continue? (y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_info "Operation cancelled."
    exit 0
fi

echo ""
print_info "Setting up logging environment..."

# Create logs directory if it doesn't exist
mkdir -p logs

print_success "Created logs directory: ./logs/"

echo ""
print_info "Starting application with logging profile..."
print_info "Log files will be saved to: ./logs/zaina-application.log"
echo ""

print_warning "To test the logging system:"
echo "1. Open another terminal and run: tail -f logs/zaina-application.log"
echo "2. Open the frontend example: open frontend-websocket-example.html"
echo "3. Make API requests to see HTTP logging in action"
echo "4. Connect via WebSocket to see real-time event logging"
echo ""

print_info "Starting application now..."
echo "============================================================================================================"

# Start the application with the logging profile
export SPRING_PROFILES_ACTIVE=logging
./mvnw spring-boot:run 