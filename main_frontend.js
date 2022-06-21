const express = require('express');
const cors = require('cors');
const path = require('path');
const PORT = process.env.PORT || 3000;

const app = express();
app.use(cors());

const dist = 'src/main/react/dist'
app.use(express.static(`${dist}`));

app.get('*', (_req, res) => {
  // Send to the client all the built frontend
  res.sendFile(path.resolve(__dirname, dist, 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}`);
});
