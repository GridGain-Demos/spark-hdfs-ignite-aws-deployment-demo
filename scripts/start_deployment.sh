#!/usr/bin/env bash

set -x

time ANSIBLE_HOST_KEY_CHECKING=False  ansible-playbook $WEBINAR_HOME/ansible_provisioning_script/site.yml --extra-vars "deployment_command=provision"
