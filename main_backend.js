const { spawn } = require('child_process');

// Load environment variables to process.env
require('dotenv').config();

// Run Spring API
const server_process = spawn('java', ['-jar', 'target/ule-chat-0.0.1.jar'], {
  detached: false,
  killSignal: "SIGINT",
  timeout: 0,
});

server_process.stdout.on("data", (data) => {
  console.log(data.toString());
});

server_process.stderr.on("data", (data) => {
  console.log(data.toString());
});

server_process.on("close", (code) => {
  console.log(`Exited with code ${code}`);
  process.exit(code);
});
