from fpdf import FPDF
import os

class MsaManualPDF(FPDF):
    def __init__(self):
        super().__init__()
        font_path = "C:/Windows/Fonts"
        self.add_font("malgun", "", os.path.join(font_path, "malgun.ttf"))
        self.add_font("malgun", "B", os.path.join(font_path, "malgunbd.ttf"))
        self.set_auto_page_break(auto=True, margin=20)

    def header(self):
        if self.page_no() > 1:
            self.set_font("malgun", "B", 8)
            self.set_text_color(150, 150, 150)
            self.cell(0, 8, "JIUCOM MSA Infrastructure Manual", align="R")
            self.ln(5)
            self.set_draw_color(220, 220, 220)
            self.line(10, self.get_y(), 200, self.get_y())
            self.ln(5)

    def footer(self):
        self.set_y(-15)
        self.set_font("malgun", "", 8)
        self.set_text_color(150, 150, 150)
        self.cell(0, 10, f"- {self.page_no()} -", align="C")

    def cover_page(self):
        self.add_page()
        self.ln(50)
        self.set_fill_color(30, 50, 100)
        self.rect(0, 45, 210, 90, "F")

        self.set_y(55)
        self.set_font("malgun", "B", 32)
        self.set_text_color(255, 255, 255)
        self.cell(0, 16, "JIUCOM", align="C")
        self.ln(14)
        self.set_font("malgun", "B", 18)
        self.cell(0, 12, "MSA Infrastructure Manual", align="C")
        self.ln(14)
        self.set_font("malgun", "", 13)
        self.set_text_color(180, 200, 240)
        self.cell(0, 10, "Docker + Monitoring + API Gateway + Distributed Tracing", align="C")
        self.ln(10)
        self.set_font("malgun", "", 11)
        self.set_text_color(160, 180, 220)
        self.cell(0, 8, "9-Service Architecture Guide", align="C")

        self.set_y(160)
        self.set_text_color(80, 80, 80)
        self.set_font("malgun", "", 11)
        info = [
            ("Document", "MSA Infrastructure Manual v1.0"),
            ("Date", "2026-02-27"),
            ("Project", "JIUCOM API Platform"),
            ("Stack", "Spring Boot + Docker + Prometheus + Grafana"),
        ]
        for label, value in info:
            self.set_x(40)
            self.set_font("malgun", "B", 11)
            self.cell(35, 9, label, align="R")
            self.set_font("malgun", "", 11)
            self.cell(5, 9, "  :  ")
            self.cell(80, 9, value)
            self.ln(9)

    def section_title(self, number, title):
        self.ln(6)
        self.set_fill_color(30, 50, 100)
        self.rect(10, self.get_y(), 4, 10, "F")
        self.set_x(18)
        self.set_font("malgun", "B", 16)
        self.set_text_color(30, 50, 100)
        self.cell(0, 10, f"{number}. {title}")
        self.ln(14)
        self.set_text_color(50, 50, 50)

    def sub_title(self, title):
        self.ln(3)
        self.set_font("malgun", "B", 12)
        self.set_text_color(50, 70, 120)
        self.cell(0, 8, f">> {title}")
        self.ln(10)
        self.set_text_color(50, 50, 50)

    def body_text(self, text):
        self.set_font("malgun", "", 10)
        self.set_text_color(60, 60, 60)
        self.multi_cell(0, 7, text)
        self.ln(2)

    def bullet(self, text, indent=15):
        self.set_font("malgun", "", 10)
        self.set_text_color(60, 60, 60)
        self.set_x(indent)
        self.multi_cell(0, 7, f"*  {text}")
        self.ln(1)

    def code_block(self, text):
        self.ln(2)
        self.set_fill_color(245, 245, 245)
        self.set_draw_color(200, 200, 200)
        lines = text.strip().split("\n")
        block_h = len(lines) * 6 + 8
        y_start = self.get_y()

        if y_start + block_h > self.h - 20:
            self.add_page()
            y_start = self.get_y()

        self.rect(12, y_start, 186, block_h, "DF")
        self.set_font("malgun", "", 8)
        self.set_text_color(40, 40, 40)
        self.set_y(y_start + 4)
        for line in lines:
            self.set_x(16)
            self.cell(0, 6, line)
            self.ln(6)
        self.ln(4)
        self.set_text_color(50, 50, 50)

    def table(self, headers, data, col_widths=None):
        if col_widths is None:
            col_widths = [190 / len(headers)] * len(headers)

        self.set_font("malgun", "B", 9)
        self.set_fill_color(30, 50, 100)
        self.set_text_color(255, 255, 255)
        for i, h in enumerate(headers):
            self.cell(col_widths[i], 9, h, border=1, align="C", fill=True)
        self.ln()

        self.set_font("malgun", "", 9)
        self.set_text_color(50, 50, 50)
        fill = False
        for row in data:
            if fill:
                self.set_fill_color(235, 240, 250)
            else:
                self.set_fill_color(255, 255, 255)

            max_h = 9
            for i, cell in enumerate(row):
                lines = self.multi_cell(col_widths[i], 9, cell, dry_run=True, output="LINES")
                if len(lines) * 9 > max_h:
                    max_h = len(lines) * 9

            x_start = self.get_x()
            y_start = self.get_y()

            if y_start + max_h > self.h - 20:
                self.add_page()
                y_start = self.get_y()

            for i, cell in enumerate(row):
                self.set_xy(x_start + sum(col_widths[:i]), y_start)
                self.multi_cell(col_widths[i], 9, cell, border=1, align="C" if i == 0 else "L", fill=True)
            self.set_y(y_start + max_h)
            fill = not fill
        self.ln(4)

    def highlight_box(self, text, color=(220, 235, 255)):
        self.set_fill_color(*color)
        self.set_font("malgun", "", 10)
        self.set_text_color(30, 50, 100)
        y = self.get_y()
        self.rect(12, y, 186, 12, "F")
        self.set_xy(16, y + 2)
        self.cell(0, 8, text)
        self.ln(16)
        self.set_text_color(50, 50, 50)


def build_pdf():
    pdf = MsaManualPDF()

    # ========== COVER ==========
    pdf.cover_page()

    # ========== TABLE OF CONTENTS ==========
    pdf.add_page()
    pdf.set_font("malgun", "B", 20)
    pdf.set_text_color(30, 50, 100)
    pdf.cell(0, 15, "Table of Contents", align="C")
    pdf.ln(20)

    toc = [
        ("1", "Architecture Overview", "9-Service MSA"),
        ("2", "Service Details", "Each Container Role"),
        ("3", "Nginx API Gateway", "Reverse Proxy + Load Balancing"),
        ("4", "Monitoring Stack", "Prometheus + Grafana"),
        ("5", "Distributed Tracing", "Zipkin + Micrometer"),
        ("6", "Docker Compose", "Orchestration + Resource Limits"),
        ("7", "Data Flow", "Request Lifecycle"),
        ("8", "Dashboard & Alerts", "Grafana Panels"),
        ("9", "Operations Guide", "Commands + Troubleshooting"),
    ]
    for num, title, desc in toc:
        pdf.set_font("malgun", "B", 12)
        pdf.set_text_color(30, 50, 100)
        pdf.cell(12, 10, num + ".")
        pdf.set_text_color(50, 50, 50)
        pdf.cell(70, 10, title)
        pdf.set_font("malgun", "", 10)
        pdf.set_text_color(130, 130, 130)
        pdf.cell(0, 10, desc)
        pdf.ln(10)
        pdf.set_draw_color(230, 230, 230)
        pdf.line(10, pdf.get_y(), 200, pdf.get_y())
        pdf.ln(2)

    # ========================================
    # 1. Architecture Overview
    # ========================================
    pdf.add_page()
    pdf.section_title("1", "Architecture Overview")

    pdf.sub_title("Overall Architecture")
    pdf.body_text(
        "JIUCOM uses a microservice-oriented Docker Compose architecture with 9 services. "
        "The system is designed around the following principles:\n\n"
        "1) API Gateway Pattern - Nginx serves as the single entry point, routing requests "
        "to backend services and handling cross-cutting concerns (rate limiting, CORS, security headers).\n\n"
        "2) Observability Stack - Prometheus collects metrics from all services, Grafana provides "
        "visualization dashboards, and Zipkin enables distributed tracing.\n\n"
        "3) Sidecar Exporters - Redis Exporter and MySQL Exporter run as sidecar containers, "
        "translating native database metrics into Prometheus-compatible format.\n\n"
        "4) Resource Isolation - Every container has CPU/memory limits to prevent resource starvation "
        "and simulate production-grade infrastructure."
    )

    pdf.sub_title("Service Topology")
    pdf.code_block(
        "                    [Client Browser]\n"
        "                          |\n"
        "                     port 80 (HTTP)\n"
        "                          |\n"
        "                    +-----v------+\n"
        "                    |   Nginx    |  API Gateway\n"
        "                    +--+---+---+-+\n"
        "                       |   |   |\n"
        "          /api/v1/*    |   |   |  /zipkin/*\n"
        "       +---------------+   |   +-------------+\n"
        "       |           /grafana/*                 |\n"
        "       v                   v                  v\n"
        "  +----+-----+      +-----+-----+     +------+----+\n"
        "  | App:8080 |      |Grafana:3000|     |Zipkin:9411|\n"
        "  +--+----+--+      +-----+------+     +-----------+\n"
        "     |    |               |\n"
        "     |    |         +-----v--------+\n"
        "     |    |         |Prometheus:9090|\n"
        "     |    |         +--+-----+---+-+\n"
        "     |    |            |     |   |\n"
        "     v    v            v     v   v\n"
        "  MySQL  Redis      App  Redis  MySQL\n"
        "  :3306  :6379    metrics Exp.  Exp.\n"
        "                         :9121  :9104"
    )

    # ========================================
    # 2. Service Details
    # ========================================
    pdf.add_page()
    pdf.section_title("2", "Service Details")

    pdf.sub_title("All 9 Services")
    pdf.table(
        ["Service", "Image", "Port", "Role", "Resources"],
        [
            ["mysql", "mysql:8.0", "3306", "Primary database\n(user, part, build, post...)", "2 CPU / 2GB"],
            ["redis", "redis:7-alpine", "6379", "Cache + session store\n(price cache, rate limit)", "1 CPU / 512MB"],
            ["app", "Spring Boot\n(custom build)", "8080", "JIUCOM API server\n(70+ REST endpoints)", "2 CPU / 1GB"],
            ["nginx", "nginx:alpine", "80", "API Gateway\nreverse proxy", "0.5 CPU / 256MB"],
            ["prometheus", "prom/prometheus", "9090", "Metrics collection\ntime-series DB", "0.5 CPU / 512MB"],
            ["grafana", "grafana/grafana", "3000", "Monitoring dashboard\nvisualization", "0.5 CPU / 512MB"],
            ["redis-exp", "oliver006/\nredis_exporter", "9121", "Redis metrics\nexporter", "0.25 CPU / 128MB"],
            ["mysql-exp", "prom/\nmysqld-exporter", "9104", "MySQL metrics\nexporter", "0.25 CPU / 128MB"],
            ["zipkin", "openzipkin/\nzipkin", "9411", "Distributed tracing\nrequest tracking", "0.5 CPU / 512MB"],
        ],
        [22, 30, 15, 55, 30]
    )

    pdf.sub_title("Service Dependency Chain")
    pdf.body_text(
        "Services start in dependency order:\n\n"
        "1) mysql, redis (independent, start first with health checks)\n"
        "2) app (depends on mysql + redis being healthy)\n"
        "3) prometheus, zipkin (depend on app)\n"
        "4) grafana (depends on prometheus)\n"
        "5) redis-exporter, mysql-exporter (depend on redis/mysql)\n"
        "6) nginx (depends on app, grafana, zipkin - starts last)"
    )

    # ========================================
    # 3. Nginx API Gateway
    # ========================================
    pdf.add_page()
    pdf.section_title("3", "Nginx API Gateway")

    pdf.sub_title("Role & Responsibilities")
    pdf.body_text(
        "Nginx acts as the API Gateway - the single entry point for all external traffic. "
        "This is a core MSA pattern that provides:"
    )
    pdf.bullet("Reverse Proxy - Routes requests to the correct backend service based on URL path")
    pdf.bullet("Rate Limiting - Protects backend from DDoS/abuse (30 req/sec per IP, burst 50)")
    pdf.bullet("Security Headers - X-Frame-Options, X-Content-Type-Options, XSS protection")
    pdf.bullet("WebSocket Support - HTTP/1.1 Upgrade for real-time notifications (STOMP)")
    pdf.bullet("Gzip Compression - Reduces response payload size for JSON/text")

    pdf.sub_title("Routing Rules")
    pdf.table(
        ["Path", "Upstream", "Description"],
        [
            ["/api/v1/*", "app:8080", "All REST API endpoints\n(auth, parts, builds, posts...)"],
            ["/grafana/*", "grafana:3000", "Monitoring dashboard\n(Grafana UI)"],
            ["/zipkin/*", "zipkin:9411", "Distributed tracing UI\n(Zipkin web console)"],
            ["/health", "(direct)", "Nginx health check\nreturns 200 OK"],
            ["/", "(redirect)", "Redirects to\n/api/v1/swagger-ui.html"],
        ],
        [35, 40, 115]
    )

    pdf.sub_title("How Reverse Proxy Works")
    pdf.body_text(
        "When a client sends a request to http://localhost/api/v1/parts:\n\n"
        "1) Nginx receives the request on port 80\n"
        "2) The 'location /api/v1/' block matches the URL\n"
        "3) Rate limiter checks if the client IP is within the 30 req/sec limit\n"
        "4) If allowed, Nginx forwards (proxies) the request to http://app:8080/api/v1/parts\n"
        "5) Nginx adds headers: X-Real-IP, X-Forwarded-For, X-Forwarded-Proto\n"
        "6) The Spring Boot app processes the request and returns a response\n"
        "7) Nginx adds security headers and forwards the response to the client\n\n"
        "The client never communicates directly with the Spring Boot app. "
        "This provides a layer of abstraction and security."
    )

    # ========================================
    # 4. Monitoring Stack
    # ========================================
    pdf.add_page()
    pdf.section_title("4", "Monitoring Stack")

    pdf.sub_title("Prometheus - Metrics Collection")
    pdf.body_text(
        "Prometheus is a time-series database that collects metrics from all services "
        "using a pull-based model. Every 15 seconds, Prometheus scrapes (HTTP GET) "
        "each target's metrics endpoint."
    )

    pdf.highlight_box("Prometheus Pull Model: Prometheus --HTTP GET--> Target /metrics")

    pdf.body_text("Scrape targets configured in prometheus.yml:")
    pdf.table(
        ["Job Name", "Target", "Metrics Path", "What It Collects"],
        [
            ["jiucom-api", "app:8080", "/api/v1/actuator/\nprometheus", "JVM heap, GC, threads\nHTTP requests, HikariCP"],
            ["redis-exporter", "redis-exporter\n:9121", "/metrics", "Connected clients\nmemory usage, commands"],
            ["mysql-exporter", "mysql-exporter\n:9104", "/metrics", "Queries/sec, threads\nslow queries, connections"],
        ],
        [30, 35, 40, 85]
    )

    pdf.sub_title("How Spring Boot Exposes Metrics")
    pdf.body_text(
        "The Spring Boot app uses Micrometer (a metrics facade) to expose metrics:\n\n"
        "1) spring-boot-starter-actuator enables the /actuator endpoints\n"
        "2) micrometer-registry-prometheus formats metrics in Prometheus text format\n"
        "3) application.yml exposes: health, info, metrics, prometheus, loggers\n"
        "4) Prometheus scrapes GET /api/v1/actuator/prometheus every 15s\n\n"
        "Key metrics exposed:"
    )
    pdf.bullet("jvm_memory_used_bytes - JVM heap/non-heap memory usage")
    pdf.bullet("jvm_threads_live_threads - Number of active threads")
    pdf.bullet("http_server_requests_seconds_count - HTTP request count by status/method/uri")
    pdf.bullet("http_server_requests_seconds_sum - Total response time (for avg calculation)")
    pdf.bullet("hikaricp_connections_active - Active database connections")
    pdf.bullet("jvm_gc_pause_seconds_count - Garbage collection frequency")

    pdf.add_page()
    pdf.sub_title("Grafana - Visualization Dashboard")
    pdf.body_text(
        "Grafana reads data from Prometheus and displays it as visual dashboards. "
        "The JIUCOM dashboard is auto-provisioned on startup."
    )

    pdf.body_text("Auto-provisioning files (mounted as Docker volumes):")
    pdf.bullet("datasource.yml - Connects Grafana to Prometheus (http://prometheus:9090)")
    pdf.bullet("dashboard.yml - Tells Grafana to load dashboards from /var/lib/grafana/dashboards/")
    pdf.bullet("jiucom-dashboard.json - Pre-built dashboard with 16 panels")

    pdf.sub_title("Dashboard Panels (16 Total)")
    pdf.table(
        ["Section", "Panels", "Metrics Used"],
        [
            ["Application\nOverview", "API Status, Heap Usage\nThreads, Request Rate\nError Rate (5xx)", "up{}, jvm_memory_*\njvm_threads_*\nhttp_server_requests_*"],
            ["HTTP\nMetrics", "Response Time (avg)\nRequests by Status", "http_server_requests_\nseconds_sum/count"],
            ["JVM\nMetrics", "Heap Memory\n(used/committed/max)\nGC Pause Count", "jvm_memory_used_bytes\njvm_gc_pause_*"],
            ["Database\n& Cache", "HikariCP Pool\nRedis Clients\nRedis Memory", "hikaricp_connections_*\nredis_connected_clients\nredis_memory_used_bytes"],
            ["MySQL\nMetrics", "Queries/sec\nSlow Queries\nThreads", "mysql_global_status_\nqueries/threads_*"],
        ],
        [25, 50, 115]
    )

    pdf.sub_title("Exporter Pattern")
    pdf.body_text(
        "Redis and MySQL don't natively expose Prometheus metrics. "
        "Exporters are sidecar containers that:\n\n"
        "1) Connect to the database (Redis/MySQL) using native protocol\n"
        "2) Query internal statistics (INFO, SHOW GLOBAL STATUS)\n"
        "3) Translate those stats into Prometheus metric format\n"
        "4) Expose them on /metrics endpoint for Prometheus to scrape\n\n"
        "This is the standard Prometheus 'Exporter' pattern for third-party systems."
    )

    # ========================================
    # 5. Distributed Tracing
    # ========================================
    pdf.add_page()
    pdf.section_title("5", "Distributed Tracing (Zipkin)")

    pdf.sub_title("What is Distributed Tracing?")
    pdf.body_text(
        "In a microservice architecture, a single user request may pass through "
        "multiple services. Distributed tracing tracks the entire journey of a request "
        "across all services, showing:\n\n"
        "- Which services were called\n"
        "- How long each service took\n"
        "- Where bottlenecks or errors occurred\n\n"
        "Each request gets a unique Trace ID, and each service call within that request "
        "gets a Span ID."
    )

    pdf.sub_title("How It Works in JIUCOM")
    pdf.body_text(
        "1) Client sends request to Nginx -> forwarded to Spring Boot app\n\n"
        "2) Micrometer Tracing (Brave bridge) automatically:\n"
        "   - Generates a Trace ID for the request\n"
        "   - Creates a Span for each operation (HTTP handler, DB query, Redis call)\n"
        "   - Propagates trace context via HTTP headers (B3 propagation)\n\n"
        "3) When the request completes, spans are sent asynchronously to Zipkin\n"
        "   via HTTP POST to http://zipkin:9411/api/v2/spans\n\n"
        "4) Zipkin stores the trace data (in-memory for dev, could use Elasticsearch for prod)\n\n"
        "5) Zipkin UI (port 9411) visualizes the trace as a timeline/waterfall diagram"
    )

    pdf.sub_title("Dependencies Added")
    pdf.code_block(
        "// build.gradle.kts\n"
        "implementation(\"io.micrometer:micrometer-tracing-bridge-brave\")\n"
        "implementation(\"io.zipkin.reporter2:zipkin-reporter-brave\")\n"
        "\n"
        "// application.yml\n"
        "management.tracing.sampling.probability: 1.0  (dev: 100%)\n"
        "management.zipkin.tracing.endpoint: http://zipkin:9411/api/v2/spans"
    )

    pdf.sub_title("Sampling Strategy")
    pdf.table(
        ["Profile", "Sampling Rate", "Reason"],
        [
            ["dev", "1.0 (100%)", "Trace every request for debugging"],
            ["prod", "0.1 (10%)", "Reduce overhead, sample enough for analysis"],
        ],
        [30, 40, 120]
    )

    # ========================================
    # 6. Docker Compose Orchestration
    # ========================================
    pdf.add_page()
    pdf.section_title("6", "Docker Compose Orchestration")

    pdf.sub_title("Container Resource Limits")
    pdf.body_text(
        "Every container has CPU and memory limits to prevent resource starvation. "
        "This simulates production-grade Kubernetes resource management."
    )
    pdf.table(
        ["Service", "CPU Limit", "Memory Limit", "CPU Reserve", "Memory Reserve"],
        [
            ["app", "2.0", "1 GB", "0.5", "512 MB"],
            ["mysql", "2.0", "2 GB", "0.5", "512 MB"],
            ["redis", "1.0", "512 MB", "0.25", "128 MB"],
            ["nginx", "0.5", "256 MB", "0.1", "64 MB"],
            ["prometheus", "0.5", "512 MB", "0.25", "256 MB"],
            ["grafana", "0.5", "512 MB", "0.25", "256 MB"],
            ["zipkin", "0.5", "512 MB", "0.25", "256 MB"],
            ["redis-exp", "0.25", "128 MB", "0.1", "64 MB"],
            ["mysql-exp", "0.25", "128 MB", "0.1", "64 MB"],
        ],
        [30, 30, 35, 35, 35]
    )

    pdf.sub_title("Network Architecture")
    pdf.body_text(
        "All services share a single Docker bridge network: jiucom-network\n\n"
        "Within this network, containers communicate using service names as DNS hostnames. "
        "For example, the app container connects to 'mysql:3306' and 'redis:6379'. "
        "Docker's embedded DNS resolves these names to container IP addresses.\n\n"
        "Only specific ports are published to the host machine:\n"
        "- Port 80 (Nginx) - main entry point\n"
        "- Port 8080 (App) - direct API access (for development)\n"
        "- Port 3000 (Grafana), 9090 (Prometheus), 9411 (Zipkin) - monitoring"
    )

    pdf.sub_title("Volume Persistence")
    pdf.table(
        ["Volume", "Container Path", "Purpose"],
        [
            ["mysql-data", "/var/lib/mysql", "MySQL data files\n(survives container restart)"],
            ["redis-data", "/data", "Redis RDB snapshots"],
            ["prometheus-data", "/prometheus", "Time-series metrics data\n(15 day retention)"],
            ["grafana-data", "/var/lib/grafana", "Grafana settings, users\n(not dashboards - those are provisioned)"],
        ],
        [40, 50, 100]
    )

    # ========================================
    # 7. Data Flow
    # ========================================
    pdf.add_page()
    pdf.section_title("7", "Data Flow - Request Lifecycle")

    pdf.sub_title("Example: GET /api/v1/parts (Search Parts)")
    pdf.body_text(
        "Step-by-step flow when a client searches for computer parts:"
    )

    pdf.highlight_box("Phase 1: Request Ingress")
    pdf.bullet("Client sends HTTP GET http://localhost/api/v1/parts?category=CPU")
    pdf.bullet("Nginx receives on port 80, matches 'location /api/v1/'")
    pdf.bullet("Rate limiter checks: client IP within 30 req/sec? If yes, proceed")
    pdf.bullet("Nginx proxies to http://app:8080/api/v1/parts?category=CPU")
    pdf.bullet("Adds headers: X-Real-IP, X-Forwarded-For, X-Forwarded-Proto")

    pdf.highlight_box("Phase 2: Application Processing")
    pdf.bullet("Spring Boot receives request, Micrometer creates Trace ID + Span")
    pdf.bullet("JwtAuthenticationFilter checks Authorization header (if present)")
    pdf.bullet("RequestLoggingFilter logs: method, URI, client IP, start time")
    pdf.bullet("PartController.searchParts() invoked by DispatcherServlet")
    pdf.bullet("PartService calls PartRepository (QueryDSL dynamic query)")
    pdf.bullet("HikariCP provides a connection from the pool to MySQL")
    pdf.bullet("Query executes, results mapped to PartSearchResponse DTOs")

    pdf.highlight_box("Phase 3: Response Egress")
    pdf.bullet("ApiResponse<T> wraps the result: {success: true, data: [...], message: ...}")
    pdf.bullet("Micrometer records: http_server_requests_seconds (duration, status, uri)")
    pdf.bullet("Trace span sent async to Zipkin (http://zipkin:9411/api/v2/spans)")
    pdf.bullet("Response returns through Nginx, security headers added")
    pdf.bullet("Client receives JSON response with parts data")

    pdf.add_page()
    pdf.highlight_box("Phase 4: Metrics Collection (Background)")
    pdf.bullet("Every 15 seconds, Prometheus scrapes GET /api/v1/actuator/prometheus")
    pdf.bullet("Metrics collected: request count, response time, JVM heap, thread count")
    pdf.bullet("Prometheus stores in TSDB (time-series database), 15-day retention")
    pdf.bullet("Grafana queries Prometheus via PromQL for dashboard visualization")
    pdf.bullet("Redis Exporter scrapes Redis INFO, MySQL Exporter scrapes SHOW STATUS")

    pdf.sub_title("Monitoring Data Flow")
    pdf.code_block(
        "Spring Boot App ----metrics----> Prometheus ----query----> Grafana\n"
        "       |                              ^                      |\n"
        "       |                              |                    [Dashboard]\n"
        "    [traces]                    Redis Exporter\n"
        "       |                              ^\n"
        "       v                              |\n"
        "    Zipkin                          Redis\n"
        "       |                              \n"
        "    [UI]                        MySQL Exporter\n"
        "                                      ^\n"
        "                                      |\n"
        "                                    MySQL"
    )

    # ========================================
    # 8. Dashboard & Alerts
    # ========================================
    pdf.add_page()
    pdf.section_title("8", "Dashboard & Alert Design")

    pdf.sub_title("Grafana Dashboard Layout")
    pdf.body_text(
        "The pre-provisioned JIUCOM Dashboard (UID: jiucom-main) has 4 row sections "
        "with 16 panels total. Auto-refresh is set to every 10 seconds."
    )

    pdf.table(
        ["Row", "Panel Type", "What to Watch", "Warning Sign"],
        [
            ["Overview\n(Row 1)", "Stat panels x5", "API UP status\nHeap usage %\nHTTP request rate", "DOWN status\nHeap > 90%\nRate spike/drop"],
            ["HTTP\n(Row 2)", "Time series x2", "Avg response time\nRequests by status code", "Latency > 1s\n5xx errors > 1%"],
            ["JVM\n(Row 3)", "Time series x2", "Heap memory trend\nGC pause frequency", "Memory climbing\nFrequent full GC"],
            ["DB/Cache\n(Row 4)", "Time series x5", "HikariCP pool usage\nRedis clients/memory\nMySQL queries/threads", "Pool exhaustion\nRedis memory full\nSlow query spike"],
        ],
        [22, 35, 55, 55]
    )

    pdf.sub_title("Key PromQL Queries")
    pdf.code_block(
        "# API Server Up/Down\n"
        "up{job=\"jiucom-api\"}\n"
        "\n"
        "# JVM Heap Usage Percentage\n"
        "jvm_memory_used_bytes{area=\"heap\"} / jvm_memory_max_bytes{area=\"heap\"} * 100\n"
        "\n"
        "# HTTP Request Rate (per second, 5m average)\n"
        "rate(http_server_requests_seconds_count{job=\"jiucom-api\"}[5m])\n"
        "\n"
        "# HTTP 5xx Error Rate (%)\n"
        "rate(http_server_requests_seconds_count{status=~\"5..\"}[5m])\n"
        "  / rate(http_server_requests_seconds_count[5m]) * 100\n"
        "\n"
        "# Average Response Time (seconds)\n"
        "rate(http_server_requests_seconds_sum[5m])\n"
        "  / rate(http_server_requests_seconds_count[5m])"
    )

    # ========================================
    # 9. Operations Guide
    # ========================================
    pdf.add_page()
    pdf.section_title("9", "Operations Guide")

    pdf.sub_title("Start / Stop Commands")
    pdf.code_block(
        "# Start all 9 services (build app image first)\n"
        "docker compose up -d --build\n"
        "\n"
        "# Start without rebuilding\n"
        "docker compose up -d\n"
        "\n"
        "# Stop all services (keep data volumes)\n"
        "docker compose down\n"
        "\n"
        "# Stop and delete all data (DESTRUCTIVE)\n"
        "docker compose down -v\n"
        "\n"
        "# View logs for specific service\n"
        "docker compose logs -f app\n"
        "docker compose logs -f nginx\n"
        "\n"
        "# Restart single service\n"
        "docker compose restart app"
    )

    pdf.sub_title("Health Check URLs")
    pdf.table(
        ["Service", "URL", "Expected"],
        [
            ["API (via Nginx)", "http://localhost/api/v1/\nactuator/health", "200 OK\n{status: UP}"],
            ["API (direct)", "http://localhost:8080/\napi/v1/actuator/health", "200 OK\n{status: UP}"],
            ["Prometheus", "http://localhost:9090/\ntargets", "3 targets UP"],
            ["Grafana", "http://localhost:3000/\ngrafana/login", "Login page\n(admin/admin)"],
            ["Zipkin", "http://localhost:9411/\nzipkin/", "Zipkin UI"],
            ["Swagger", "http://localhost/api/v1/\nswagger-ui.html", "Swagger UI"],
        ],
        [30, 60, 55]
    )

    pdf.add_page()
    pdf.sub_title("Troubleshooting")
    pdf.table(
        ["Problem", "Diagnosis", "Solution"],
        [
            ["App won't start", "docker compose logs app\nCheck DB connection", "Ensure mysql is healthy\nCheck DB_HOST env var"],
            ["Prometheus\nno targets", "http://localhost:9090\n/targets", "Check prometheus.yml\nEnsure app is running"],
            ["Grafana\nno data", "Check datasource\nsettings in Grafana", "Verify Prometheus URL\nhttp://prometheus:9090"],
            ["Nginx\n502 Bad Gateway", "docker compose logs nginx\nCheck upstream", "Ensure app container\nis running and healthy"],
            ["Zipkin\nno traces", "Check app logs for\nZipkin reporter errors", "Verify ZIPKIN_ENDPOINT\nenv var in app"],
            ["MySQL exporter\nconnection refused", "docker compose logs\nmysql-exporter", "Check DATA_SOURCE_NAME\nMySQL must be healthy"],
        ],
        [30, 50, 55]
    )

    pdf.sub_title("Scaling Considerations")
    pdf.body_text(
        "For production deployment, consider these upgrades:\n\n"
        "1) Nginx: Add SSL/TLS termination with Let's Encrypt certificates\n"
        "2) App: Scale horizontally with 'docker compose up --scale app=3'\n"
        "3) Zipkin: Switch from in-memory to Elasticsearch storage\n"
        "4) Prometheus: Add alertmanager for Slack/email notifications\n"
        "5) Grafana: Configure SMTP for scheduled report delivery\n"
        "6) MySQL: Add read replicas for query distribution\n"
        "7) Redis: Enable Redis Cluster for high availability"
    )

    pdf.ln(8)
    pdf.set_draw_color(30, 50, 100)
    pdf.line(10, pdf.get_y(), 200, pdf.get_y())
    pdf.ln(8)
    pdf.set_font("malgun", "B", 12)
    pdf.set_text_color(30, 50, 100)
    pdf.cell(0, 10, "JIUCOM - Production-Ready MSA Infrastructure", align="C")
    pdf.ln(10)
    pdf.set_font("malgun", "", 10)
    pdf.set_text_color(100, 100, 100)
    pdf.cell(0, 8, "Document generated: 2026-02-27  |  JIUCOM Team", align="C")

    # ========== OUTPUT ==========
    output_path = r"C:\Vibecode\JIUCOM\JIUCOM_MSA_Manual.pdf"
    pdf.output(output_path)
    print(f"PDF generated: {output_path}")


if __name__ == "__main__":
    build_pdf()
