# [Omni-Moderation](https://www.SpigotMC.org/resources/omni-moderation.127052/)

## Description

The Omni-Moderation Plugin is a chat moderation tool for Minecraft servers.
It intercepts **player chat messages**, checks them using [OpenAI's Omni Moderation API](https://platform.openai.com/docs/guides/moderation/), and either blocks or allows the content.
All messages are logged, and repeated content is cached to reduce API usage.

The plugin uses my [Omni-Moderation Java Maven Library](https://www.GitHub.com/MCmoderSD/Omni-Moderation/) for seamless API integration, request handling, and moderation result parsing.
This external library cleanly separates API logic from plugin functionality.

## How It Works

Unlike traditional filters that block individual keywords, Omni-Moderation moderates the **entire chat message in context**.

* It does **not block words** directly, but rather evaluates the **meaning and intent** of the full message.
* This allows the AI to distinguish between truly harmful content and acceptable expressions that might include sensitive terms.
* For example, it can detect toxic intent even if the message is disguised as `N1****` instead of `Ni****`, while allowing non-offensive uses like `I love all Ni****`.

This makes the system **harder to bypass** using common tricks like character substitution, but also introduces some limitations (see Disclaimer).

All chat messages are **logged**, allowing you to review what was allowed or blocked. This logging also makes it possible to **monitor users who frequently trigger the moderation system**. These users can then be **manually punished** if necessary, based on server policy.

> The plugin **only logs and monitors** flagged messages. What you do with the logs is **entirely up to you**.

## Features

* AI moderation via [OpenAI](https://www.OpenAI.com/)
* Chat message logging (blocked and allowed)
* SQLite cache with compressed database storage
* Customizable moderation profiles
* Whitelist/Blacklist commands for manual overrides
* Message logging enables detection of suspicious users

## Requirements

* Java **21**
* Minecraft **1.21 or higher**
* A valid **OpenAI API key** (required, but free to use under rate limits)

To use the plugin, you must sign up at [OpenAI](https://platform.openai.com/signup) and create an API key.
After signing in, you can find your:

* **API key** in your [API keys page](https://platform.openai.com/account/api-keys)
* **Project ID** in your [organization projects page](https://platform.openai.com/settings/organization/projects)
* **Organization ID** in your [organization settings page](https://platform.openai.com/settings/organization)

## Default Configuration (`config.yml`)

```yaml
# OpenAI API configuration
api_key: "your_api_key_here"                # Required
Project: "your_project_id_here"             # Optional
Organization: "your_organization_id_here"   # Optional

# Choose one of the following profiles:
# FULL   = Flagged at any score
# HIGH   = Flagged at 35% or more
# MEDIUM = Flagged at 55% or more
# LOW    = Flagged at 95% or more
# MINIMAL= Flagged at 99% or more
# NONE   = Monitoring only, no flags
Profile: FULL
```

## Moderation Profiles

Select how strictly content is flagged:

* `FULL`    – Flag anything suspicious
* `HIGH`    – 35%+ flagged
* `MEDIUM`  – 55%+
* `LOW`     – 95%+
* `MINIMAL` – 99%+
* `NONE`    – Only monitors, does not block

> **Tip:** After using `NONE`, reset the database to avoid whitelisting undesired messages.

## Commands

Only accessible by moderators:

* `/moderation status` – Show plugin status
* `/moderation whitelist <content>` – Whitelist specific text
* `/moderation blacklist <content>` – Blacklist specific text
* `/moderation reset` – Clear the moderation database
* `/configure profile <FULL|...>` – Set moderation profile
* `/configure apikey|projectid|organizationid` – View current setting
* `/configure apikey|projectid|organizationid <value>` – Set credentials

## Disclaimer

Omni-Moderation is a solid **first line of defense** for chat moderation, but it is **not a complete replacement** for human review or broader server moderation policies.

* It relies on OpenAI’s context-aware moderation model.
* This model can sometimes allow positively framed but inappropriate messages like `I love all Ni****`, which are not flagged.
* At the same time, it is robust against obfuscation tricks like `N1****`, recognizing toxic intent even when the text is masked.
* These limitations are rooted in the OpenAI moderation model and **cannot be fixed by this plugin**.

I recommend combining this with:

* A manual review system
* Server rules enforcement
* Ongoing updates to whitelists and blacklists

---

Built for server admins who want AI-driven moderation with performance and flexibility in mind.