#!/usr/bin/env bash

set -x

time ANSIBLE_HOST_KEY_CHECKING=False  ansible-playbook $WEBINAR_HOME/ansible_start_tools/site.yml -i $WEBINAR_HOME/ansible_start_tools/production
