#!/usr/bin/env bash

set -x

time ANSIBLE_HOST_KEY_CHECKING=False  ansible-playbook $WEBINAR_HOME/ansible_maintenance_scripts/main.yml -i $WEBINAR_HOME/ansible_maintenance_scripts/production
