# nano-vwap

Real-time VWAP calculator connected to Alpaca WebSocket trade feed.

Subscribes to live trade events, computes Volume Weighted Average Price on the fly.

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
