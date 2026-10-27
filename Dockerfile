# ====================================================================
# DOCKERFILE - Deploy Anywhere (AWS, DigitalOcean, Render, Railway)
# ====================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY . .

# Compile Java sources
RUN mkdir -p bin && \
    javac -encoding UTF-8 -cp "lib/*" -d bin \
    src/com/hostel/config/*.java \
    src/com/hostel/security/*.java \
    src/com/hostel/exception/*.java \
    src/com/hostel/model/*.java \
    src/com/hostel/dao/*.java \
    src/com/hostel/service/*.java \
    src/com/hostel/server/*.java \
    src/com/hostel/Main.java

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/bin ./bin
COPY --from=builder /app/lib ./lib
COPY --from=builder /app/web ./web
COPY --from=builder /app/database ./database
COPY --from=builder /app/config ./config

EXPOSE 8080 8443

# Run Dual-Mode Server
CMD ["java", "-cp", "bin:lib/*", "com.hostel.Main", "8080"]
