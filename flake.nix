{
  inputs = {
    typelevel-nix.url = "github:typelevel/typelevel-nix";
    nixpkgs.follows = "typelevel-nix/nixpkgs";
    flake-utils.follows = "typelevel-nix/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils, typelevel-nix }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ typelevel-nix.overlay ];
        };
      in
      {
        devShell = pkgs.devshell.mkShell {
          imports = [ typelevel-nix.typelevelShell ];
          name = "named-codec-shell";
          typelevelShell = {
            jdk.package = pkgs.jdk8;
            nodejs.enable = true;
          };
        };
      }
    );
}
