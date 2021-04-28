from __future__ import (absolute_import, division, print_function)
__metaclass__ = type

import os

from ansible import constants as C
from ansible.plugins.action import ActionBase

boolean = C.mk_boolean

class ActionModule(ActionBase):

    def run(self, tmp=None, task_vars=None):
        ''' Pre-processes ec2 instance provisioning'''

        if task_vars is None:
            task_vars = dict()

        result = super(ActionModule, self).run(tmp, task_vars)

        volumes = self._task.args.get('volumes', [])

        new_volumes = []

        for name in volumes:
            if boolean(task_vars.get(name + '_disk', True)):
                new_volume = {}

                new_volume['device_name'] = task_vars[name + '_disk_name']

                new_volume['volume_size'] = task_vars[name + '_disk_size']

                new_volume['volume_type'] = task_vars[name + '_disk_type']

                new_volume['delete_on_termination'] = task_vars[name + '_disk_delete_on_termination']

                if name + '_disk_iops' in task_vars and new_volume['volume_type'] == 'io1':
                    new_volume['iops'] = task_vars[name + '_disk_iops']

                new_volumes.append(new_volume)

        new_module_args = self._task.args.copy()

        # remove newline_sequence from standard arguments
        new_module_args['volumes'] = new_volumes

        result.update(self._execute_module(module_name='ec2', module_args=new_module_args, task_vars=task_vars, tmp=tmp))

        self._remove_tmp_path(tmp)

        return result
