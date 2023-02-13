const { Octokit } = require('@octokit/core');
const octokit = new Octokit({
  auth: process.env.GITHUB_TOKEN
});

const GitHubUtils = {
  getPublicKey: async function (owner, repo) {
    const { data } = await octokit.request('GET /repos/{owner}/{repo}/actions/secrets/public-key', {
      owner,
      repo
    });
    return data;
  },

  createOrUpdateSecret: async function (owner, repo, secretName, secretValue, keyId) {
    const { data } = await octokit.request('PUT /repos/{owner}/{repo}/actions/secrets/{secret_name}', {
      owner,
      repo,
      secret_name: secretName,
      encrypted_value: secretValue,
      key_id: keyId
    });
    return data;
  }
};

module.exports = {
  GitHubUtils
};
