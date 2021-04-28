#!/usr/bin/python
from __future__ import absolute_import, division, print_function
__metaclass__ = type

ANSIBLE_METADATA = {'metadata_version': '1.1',
                    'status': ['preview'],
                    'supported_by': 'ikasnacheev'}

DOCUMENTATION = r'''
---
module: ec2_improvise
short_description: Improved provision of EC2 instances
'''

EXAMPLES = r'''
- name: Provision AWS Instances
  ec2_improvise:
    instance_type: "{{ ec2_instance_type }}"
    image: "{{ ec2_ami_id }}"
    ...
    volumes: [ec2_root, ec2_db]
  register: servers

ec2_root_disk_name: "/dev/sda1"
ec2_root_disk_type: "gp2"
ec2_root_disk_size: "30"
ec2_root_disk_iops: "100"
ec2_root_disk_delete_on_termination: "true"

ec2_db_disk: "true"
ec2_db_disk_name: "/dev/sdf"
ec2_db_disk_type: "io1"
ec2_db_disk_size: "30"
ec2_db_disk_iops: "100"
ec2_db_disk_delete_on_termination: "true"
'''
