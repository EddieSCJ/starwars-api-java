# Problem Description

[Jira Issue](https://www.atlassian.com/br/software/jira)

Please include a summary of the problem origin.

# Proposed Solution

Please include the description of your solution

## Breaking Changes

If there is any breaking changes, please, consider creating a new version to be published, once we have consumers that will be affected

**Breaking change example message and description**

# Development Steps

Check the following steps once you have completed it

- [ ] Understand the problem
- [ ] Write problem description in PR
- [ ] Elaborate Solution
- [ ] Write solution description in PR
- [ ] Make changes in code
- [ ] Write Unit Tests
- [ ] Write Integration Tests
- [ ] Write or Update Documentation
- [ ] Submit PR
- [ ] Accept review comments

**Test Configuration**:
* Hardware:
* Used Tools:
  * Docker
  * Java
  * Localstack
* Java SDK:

# Checklist:

- [ ] My code follows the style guidelines of this project
- [ ] I have performed a self-review of my own code
- [ ] My changes generate no new warnings
- [ ] Any dependent changes have been merged and published in downstream modules (ex.: libs)

# Remember: Patterns
### Branches: [Jira Issue Code] - type/name
  * Ex.: [CSDD-18] - feature/planet-creation
  * Ex.: [CSDD-18] - hotfix/production-rollback
  * Ex.: [CSDD-18] - bugfix/planet-creation

### Commits
**Model:**
```
type: brief description
- detailed description
- detailed description
```

**Ex.:**
```
feature: create planet operations
- Add planet operations contratct
- Add post endpoint to create planets
- Add update planet endpoint

chore: create planet docker operations
- Add dev environment to planets
- Add dev tools: localstack and mongo to docker-compose
- Add docker compose for testing

refactor: remove code smells
- Remove public modifiers in tests
- Make variables final
```
