const { spawn } = require('child_process');

// Load environment variables to process.env
require('dotenv').config();

// Run Spring API
const server = spawn('java', ['-jar', 'target/ule-chat-0.0.1.jar']);

server.stdout.on("data", (data) => {
  console.log(data.toString());
});

server.on("close", (code) => {
  console.log(`Server exited with code ${code}`)
});
