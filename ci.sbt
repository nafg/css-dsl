inThisBuild(List(
  homepage := Some(url("https://github.com/nafg/css-dsl")),
  licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer("nafg", "Naftoli Gugenheim", "98384+nafg@users.noreply.github.com", url("https://github.com/nafg"))
  ),
  dynverGitDescribeOutput ~= (_.map(o => o.copy(dirtySuffix = sbtdynver.GitDirtySuffix("")))),
  dynverSonatypeSnapshots := true,
  githubWorkflowEnv += ("SBT_OPTS" -> "-Xmx2g"),
  githubWorkflowPermissions := Some(Permissions.Specify(Map(
    PermissionScope.Actions -> PermissionValue.Write,
    PermissionScope.Contents -> PermissionValue.Read
  ))),
  githubWorkflowScalaVersions := githubWorkflowScalaVersions.value.map(_.replaceFirst("\\.\\d+\\.\\d+$", ".x")),
  githubWorkflowGeneratedUploadSteps := Seq(
    WorkflowStep.Run(
      List("tar cf targets.tar target project/target"),
      name = Some("Compress target directories")
    ),
    WorkflowStep.Use(
      UseRef.Public("actions", "upload-artifact", "v7"),
      name = Some("Upload target directories"),
      params = Map(
        "name" -> "target-${{ matrix.os }}-${{ matrix.scala }}-${{ matrix.java }}",
        "path" -> "targets.tar"
      )
    )
  ),
  githubWorkflowTargetTags := Seq("v*"),
  githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    ),
    WorkflowStep.Run(
      List("gh workflow run regenerate-readme.yml --ref master --field release-ref=\"${GITHUB_REF_NAME}\""),
      name = Some("Regenerate README for the release"),
      env = Map("GH_TOKEN" -> "${{ secrets.GITHUB_TOKEN }}")
    )
  )
))
