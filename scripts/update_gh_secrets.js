const { GitHubUtils } = require('./github_utils');
const sodium = require('libsodium-wrappers');
const dotenv = require('dotenv');
const fs = require("fs");

GitHubUtils
        .getPublicKey('scastd00', 'rs-chat-backend')
        .then(publicKey => {
          const { key_id, key } = publicKey;

          // Check if libsodium is ready and then proceed.
          sodium.ready.then(() => {
            // Read file with the env vars and parse them.
            const env = dotenv.parse(fs.readFileSync('../env/.env.test'));

            // Convert Base64 key to Uint8Array.
            const bin_key = sodium.from_base64(key, sodium.base64_variants.ORIGINAL);

            const encryptedEnv = {};
            for (const [objKey, valueToEncrypt] of Object.entries(env)) {
              let bin_secret = sodium.from_string(valueToEncrypt);

              // Encrypt the secret using LibSodium
              let encBytes = sodium.crypto_box_seal(bin_secret, bin_key);

              // Convert encrypted Uint8Array to Base64 and add to object
              encryptedEnv[objKey] = sodium.to_base64(encBytes, sodium.base64_variants.ORIGINAL);
            }

            // Create or update the secret
            for (const [objKey, encValue] of Object.entries(encryptedEnv)) {
              GitHubUtils
                      .createOrUpdateSecret('scastd00', 'rs-chat-backend', objKey, encValue, key_id)
                      .then(() => console.log(`Secret ${objKey} updated`));
            }
          });
        });
