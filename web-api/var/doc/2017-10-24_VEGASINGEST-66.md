# ADR 003: GitLab Migration

## Status

PROPOSED

## Context

Currently, B2W uses Atlassian Stash (data repository) and Bamboo (continuous integration) tools to help in the control of projects development. Today there is the possibility of cutting part of the investment in these tools. In this way, the Vegas project team, following guidelines from the platform support area, discusses migrating these Atlassian tools to GitLab tools.

This decision provide:

- To be updated the new tools of the company and count with support of platforms;
- Knowledge in the new tools that can be extended to new projects and collaborators.

## Decision

In order to address the requirements for the technology stack we propose:

### GitLab

The replacement of Stash by GitLab is not really a concern because it's an easy-to-use platform, with already-migrated projects and many knowed features by project developers. 
To perform operations on projects such as clone, commit, push, etc, the developers will use the same current format, which facilitates the adaptation of the tool.

### GitLab-CI

GitLab-CI is the continuous integration area of GitLab. There is a configuration made in a .gitlab-ci.yml file where the stages, builds, deploy, staging, production, etc. are defined.
It could be defined for example that after a commit, the IC pipeline be triggered to perform a build and the developer will be able to identify a possible error.

## Consequences

- We expect that all this technologies give high level of productiveness and motivation for the project and team respectively, as well as greater control of develop management.

## References
- GitLab - Migrar ou não migrar ? - http://confluence.b2w/pages/viewpage.action?pageId=109578248