# Contributing to KartaBattlePass

First off, thank you for considering contributing to KartaBattlePass! It's people like you that make open source such a great community.

## Where do I go from here?

If you've noticed a bug or have a feature request, [make one](https://github.com/YourOrg/KartaBattlePass/issues/new)! It's generally best if you get confirmation of your bug or approval for your feature request this way before starting to code.

### Fork & create a branch

If this is something you think you can fix, then [fork KartaBattlePass](https://github.com/YourOrg/KartaBattlePass/fork) and create a branch with a descriptive name.

A good branch name would be (where issue #38 is the ticket you're working on):

```sh
git checkout -b 38-add-skyblock-quest-type
```

### Get the code

```sh
git clone https://github.com/your-username/KartaBattlePass.git
cd KartaBattlePass
git checkout -b my-awesome-feature
```

### Run the tests

Before you start, make sure the tests are passing:

```sh
./gradlew check
```

### Make your changes

Make your changes to the code. Please follow the existing code style. We use Google Java Format, and our build process will automatically check the formatting.

### Commit your changes

Make sure you write a good commit message.

```sh
git commit -m "feat(quests): Add Skyblock quest type"
git push origin 38-add-skyblock-quest-type
```

### Pull Request

When you're done with the changes, create a pull request, also known as a PR.

- Fill the "Ready for review" template so that we can review your PR. This template helps reviewers understand your changes as well as the purpose of your pull request.
- Don't forget to [link PR to issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) if you are solving one.
- Enable the checkbox to [allow maintainer edits](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/allowing-changes-to-a-pull-request-branch-created-from-a-fork) so the branch can be updated for a merge.
Once you submit your PR, a Karta Team member will review your proposal. We may ask questions or request additional information.

Thank you for your contribution!
