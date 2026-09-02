#!/usr/bin/env bash
#
# on-bash-failure.sh — PostToolUseFailure(Bash) hook.
#
# On every failed Bash tool call: pop a Windows toast via wsl-notify-send.exe
# and append a structured record to
#   ${CLAUDE_PROJECT_DIR}/.claude/logs/tool-call-failure-log-<yy-mm>.log
#
# Hook JSON arrives on stdin.
#
# NOTE: extract fields with `printf '%s' "$INPUT" | jq`, NEVER `echo "$INPUT" | jq`.
# Claude Code runs hook commands through /bin/sh, which is dash here; dash's
# `echo` expands \n and \t inside .error into raw U+0000..U+001F control
# characters, jq then rejects the payload as invalid JSON, and command /
# description / error all come back empty (which is the bug this script fixes).

set -eu

INPUT=$(cat)
LOG_DIR="${CLAUDE_PROJECT_DIR}/.claude/logs"
LOG="${LOG_DIR}/tool-call-failure-log-$(date +%y-%m).log"
mkdir -p "$LOG_DIR"

if command -v jq >/dev/null 2>&1; then
  CMD=$(printf '%s' "$INPUT"  | jq -r '.tool_input.command     // empty')
  DESC=$(printf '%s' "$INPUT" | jq -r '.tool_input.description // empty')
  ERR=$(printf '%s' "$INPUT"  | jq -r '.error                 // empty')
else
  CMD='' ; DESC='' ; ERR=''
fi

NOTE=${DESC:-$(printf '%s' "$CMD" | head -n1 | cut -c1-120)}
wsl-notify-send.exe --category "${WSL_DISTRO_NAME:-WSL}" \
  "Claude Code failed to call the tool: ${NOTE}" || true

{
  printf '[%s] FAILED\n' "$(date '+%Y-%m-%d %H:%M:%S')"
  printf 'command: %s\n'    "$CMD"
  printf 'description: %s\n' "$DESC"
  printf 'error: %s\n'       "$ERR"
  printf 'raw: %s\n'         "$INPUT"
  printf -- '---\n'
} >> "$LOG"
