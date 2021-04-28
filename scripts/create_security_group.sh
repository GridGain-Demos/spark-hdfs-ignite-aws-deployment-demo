#!/usr/bin/env bash

set -x

time ANSIBLE_HOST_KEY_CHECKING=False  ansible-playbook $WEBINAR_HOME/ansible_create_security_groups/security_groups.yml
