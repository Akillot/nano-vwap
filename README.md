# nano-vwap

Real-time VWAP calculator connected to Alpaca WebSocket trade feed.

Subscribes to live trade events, computes Volume Weighted Average Price on the fly.

---

## AI Bootstrap Prompt

> Copy and paste into Claude, Cursor, Codex, or GPT:

```text
You are working on nano-vwap — a Spring Boot WebSocket app that connects
to the Alpaca real-time trade feed and calculates VWAP
(Volume Weighted Average Price).

Stack: Java 21, Spring Boot 4.0.6, Spring WebSocket, Gradle, Lombok
Entry point: src/main/java/com/zozulia/nanovwap/NanoVwapApplication.java
Core logic: AlpacaWebSocketHandler.java — receives TradeMessage events and computes VWAP
Run: export ALPACA_API_KEY=... ALPACA_API_SECRET=... && ./gradlew bootRun

Non-obvious:
- Auth flow is ordered: on WS connect, send AuthMessage first,
  then SubscribeMessage with symbols — order matters
- ALPACA_API_KEY and ALPACA_API_SECRET must be set as env vars;
  they are injected via ${...} placeholders in application.properties — do not hardcode them
- VWAP calculation lives entirely in AlpacaWebSocketHandler.java — no separate service layer
- Package root: com.zozulia.nanovwap
```

---

## Tech Stack

- **Java 21** — core language
- **Spring Boot 4.0.6** — application framework
- **Spring WebSocket** — Alpaca WS client
- **Gradle** — build system
- **Lombok** — boilerplate reduction

## Quick Start

### Requirements

- Java 21+
- Alpaca account (paper or live)

### Setup

```bash
git clone https://github.com/Akillot/nano-vwap.git
cd nano-vwap
export ALPACA_API_KEY=your_key
export ALPACA_API_SECRET=your_secret
./gradlew bootRun
```

## Project Structure

```
src/main/java/com/zozulia/nanovwap/
├── NanoVwapApplication.java        # Entry point
├── dto/
│   ├── AuthMessage.java            # Alpaca auth payload
│   ├── SubscribeMessage.java       # Symbol subscription payload
│   └── TradeMessage.java           # Incoming trade event
└── websocket/
    ├── AlpacaWebSocketConfig.java  # WS connection setup
    └── AlpacaWebSocketHandler.java # Trade processing & VWAP logic
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `ALPACA_API_KEY` | Alpaca API key |
| `ALPACA_API_SECRET` | Alpaca API secret |

## License

MIT
