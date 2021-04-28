## How to use current solution
----------------------------------

Current scripts and projects were created for the quick provisioning of the AWS hosts that includes:

* Creating a subset of hosts where Apache Ignite/GridGain, HDFS and Spark will be deployed.
* Spark, HDFS and Apache Ignite/GridGain configuration
* Isolated network configuration for AWS.
* Maven project with java examples for Spark and Apache Ignite/GridGain integration.

Following packages should be installed:

|Item                     |Version
| ------ | ------ |
|ansible                  |2.9.6 or newer
|Python pip               |2.7.17 or newer
|pip boto                 |2.49.0 or newer
|pip boto3                |1.12.15 or newer
|pip botocore             |1.15.15 or newer


You can install them using the command:

```sh
$pip install -r requirements.txt
```

Possible workarounds:
```sh
sudo apt-add-repository ppa:ansible/ansible
sudo apt update
sudo apt install ansible
```

How you can check your ansible and python version:

```sh
$ansible --version
ansible 2.9.6
  config file = /etc/ansible/ansible.cfg
  configured module search path = [u'/home/andrei/.ansible/plugins/modules', u'/usr/share/ansible/plugins/modules']
  ansible python module location = /usr/lib/python2.7/dist-packages/ansible
  executable location = /usr/bin/ansible
  python version = 2.7.17 (default, Nov  7 2019, 10:07:09) [GCC 7.4.0]
```

Use `apt install` or `yum install` for upgrade your versions of ansible or python.

How you can check boto, boto3 and botocore versions:

```sh
$pip freeze
ansible==2.9.6
boto==2.49.0
boto3==1.12.37
botocore==1.15.37
```

Use `pip install` for upgrade your versions of boto packages.

## Description of existed scripts and tools

Current project contains several ansible scripts:

* `ansible_create_security_groups` - script that will create correct AWS security group for AWS hosts.
* `ansible_maintenance_scripts` - script that will configure Spark, HDFS and Apache Ignite/GridGain on AWS hosts.
* `ansible_provisioning_script` - main script that makes the provisioning operations.
* `ansible_start_tools` - script that will start Spark, HDFS and Apache Ignite/GridGain on AWS hosts.

## How to start the solution

Every ansible script will have the configuration files located in `group_vars` folder like:

* `aws_settings` - contains settings related to AWS instances, security groups, elastic load balancers S3 buckets, etc.
* `***_credentials` - contains AWS user credentials.
* `common_settings` - contains information about users, required packages, deployment mode, etc.
* `deployment_settings` -  contains information about installation pathes for binaries and directories.

## Account details

You should have the account in AWS. For internal GridGain testing, you can contact the company IT team and request the account. The following information should be prepared:

* `ssh_root_private_key_file` that will be used for accessing the AWS instances with root access. It can be created using, for example, the following link for `us-east-1` region - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#CreateKeyPair. The file should be created in `PEM` format and store on the host where you are going to start ansible scripts.
* `ec2_access_key` and `ec2_secret_key` for your account. You can generate them using the following link - https://console.aws.amazon.com/iam/home?#/security_credentials. There you can find the button `create access key`. When it will be created then you should save it somewhere in a safe place.
* `ec2_key_name` - the name of an Amazon EC2 key pair that can be used to ssh to the master node of job flow.

## Region and subnets details

Region configuration will be used in a lot of places in scripts. It's important to use the same `region` in all places. You can see the following settings:

* `ec2_region` - AWS allows to create instances in different regions. For every region, you can have a different subset of network interfaces, elastic load balancers, security groups, etc. It's important to use the same region for all scripts during deployment. Example `us-east-1` means N. Virginia.
* `ec2_zone` - every region contains several zones. When you are going to create a new instance you should choose the zone. The current parameter should be the same for all scripts. Example `us-east-1e`. The full list you can find here - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#LaunchInstanceWizard: when you will configure the instance.
* `ec2_default_vpc` - the ID of some network in the requested region that contains the subset of available addresses. You can use one of the available VPC from the list - https://console.aws.amazon.com/vpc/home?region=us-east-1#vpcs:sort=VpcId or create a new one.
* `ec2_subnet` - the subset of ip addresses that are the part of some network from the requested region. These subsets are isolated and can be found here - https://console.aws.amazon.com/vpc/home?region=us-east-1#subnets:sort=SubnetId. `ansible_create_security_groups` script will create three special subnets for jump host, monitoring hosts and gridgain cluster hosts. In case if you are going to remove these subnets then please go that link and remove it.

## Host instance details

We have three types of hosts - `jump host`, `monitoring host` and `cluster host`. During configuration you can find several properties that can be used for host instance configuration:

* `ec2_ami_id`, `ec2_monitoring_ami_id` - the ID of some amazon machine image (ami) that will be used. The full list you can find here - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#LaunchInstanceWizard:. For current project we were choosing the Centos AMI with id - `ami-02eac2c0129f6376b`.
* `ec2_instance_type`, `ec2_monitoring_instance_type` - special instance type that will be used. The full list you can find here - https://aws.amazon.com/ec2/pricing/on-demand/.
* `ec2_instance_tags`, `ec2_monitoring_instance_tags`- special name for all nodes from some group. For example, for cluster hosts you can set `ec2_instance_tags: "aalexandrov-master-cluster"`, for monitoring hosts - `ec2_monitoring_instance_tags: "master-cluster-monitoring"`, for jump host `ec2_instance_tags: "aalexandrov-jump-host"`. A lot of operations depends on these options. For example, when we should start 5 cluster nodes then we will check how many nodes started with instance tag - `aalexandrov-master-cluster`.
* `ec2_exact_count`, `ec2_monitoring_exact_count` - how many hosts should be started for grigain cluster and for monitoring.
* `ec2_security_group`, `ec2_monitoring_security_group` - security groups that were created by `ansible_create_security_groups` script. You also can use some custom groups with all open ports for testing. The full list you can find here - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#SecurityGroups:. For testing, you can use the following - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#SecurityGroup:groupId=sg-0ef3bde562d814e69.

## ELB details

In case if you are going to use the ELB discovery IP finder then you should prepare the ELB for it. It can be done manually - https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#LoadBalancers:sort=loadBalancerName using button `Create Load Balancer`. Also, it can be created using the following options:

* `ec2_elb_create` - it should be set to `true`.
* `ec2_elb_name` - the name of ELB.
* `ec2_elb_protocol` - protocol `http` or `https`
* `ec2_elb_load_balancer_port` - mandatory field. Don't used but ipfinder.
* `ec2_elb_instance_port` - mandatory field. Don't used but ipfinder.

## How to run scripts

You should prepare all configuration from above in all scripts. After that you should build the Maven project with examples from `spark_example_project` folder.

Rename file `spark-loading-from-hdfs-example-1.0-SNAPSHOT.jar` to `spark-examples.jar` and move it in `ansible_provisioning_script/files` folder. Update the following field in `common_settings`:

* `examples_jar` - "files/spark-examples.jar"

Prepare `private` and `public` keys for your user. Add `public` key into `ansible_provisioning_script/files` folder. After that modify related fields in related fields in `common_settings` files as following:

* `gridgain_rw_user` - "gg_user"
* `gridgain_rw_group` - "gg_group"
* `gridgain_rw_user_pubkey` - "files/gridgain_key.pub"
* `gridgain_rw_private_key` - "/home/andrei/aalexsandrov.pem"
* `gridgain_rw_private_key_short` - "aalexsandrov.pem"

Create environment variable $WEBINAR_HOME that should have the path to location with your scripts.

Add your local external IP in `my_cidr` field from `common_setting` file from `ansible_create_security_group` scripts as `my_cidr: "195.144.253.150/24"`.

Run first script - `create_security_group.sh`. It will generate AWS security group and you should copy name of created security group in `ec2_security_group` property from `common_settings` file. Also you should copy name of your subnet in `ec2_subnet` field from `common settings`. It should be set to all other scripts.

Now you should run the second script - `start_deployment.sh`

By default two hosts will be created in AWS after that. You need to copy external and internal IPs of these hosts - `exIP1`, `exIP2`, `intIP1`, `intIP2`.

You should choose which IP will be `master` node and `slave` node. For example `exIP1` and `intIP1` will be `master` IPs.

You should add `exIP1` and `exIP2` in `ansible_maintenance_scripts/production` file as:

```sh
[launched]
35.153.167.36
34.229.113.244
```
You should add `exIP1` in `ansible_start_tools/production` file as:

```sh
[launched]
35.153.167.36
```
Also you should add `intIP1` to `master` property and `intIP2` in `slave` property in the `ansible_maintenance_scripts/group_vars/common_settings`.

Now you can run thirs script - `setup_tools.sh`. After that you can run fourth script - `start_all_tools.sh`.

If everything was completed without error then you should be able to open UI using the following ports - 8080 (spark master),8081 (spark workers),9870 (hdfs namenode),9864 (hdfs datanodes).

Also you can use ssh  - `ssh -i /home/andrei/aalexsandrov.pem gg_user@35.153.167.36`

