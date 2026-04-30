@param String title
@param String message

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <style>
        :root {
            --bg-color: #0f172a;
            --text-color: #f8fafc;
            --accent-color: #38bdf8;
            --card-bg: #1e293b;
        }
        
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: var(--bg-color);
            color: var(--text-color);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .container {
            background-color: var(--card-bg);
            padding: 3rem;
            border-radius: 16px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
            text-align: center;
            max-width: 600px;
            width: 90%;
            border: 1px solid rgba(255, 255, 255, 0.05);
        }

        h1 {
            color: var(--accent-color);
            font-size: 2.5rem;
            margin-top: 0;
            margin-bottom: 1rem;
            font-weight: 800;
            letter-spacing: -0.025em;
        }

        p {
            font-size: 1.125rem;
            line-height: 1.75;
            color: #cbd5e1;
            margin-bottom: 2rem;
        }

        .badge {
            display: inline-block;
            background-color: rgba(56, 189, 248, 0.1);
            color: var(--accent-color);
            padding: 0.5rem 1rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
            border: 1px solid rgba(56, 189, 248, 0.2);
        }
        
        .pulse {
            animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
        }

        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: .5;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="badge pulse">Javalin 7 Ready</div>
        <h1>${title}</h1>
        <p>${message}</p>
    </div>
</body>
</html>
