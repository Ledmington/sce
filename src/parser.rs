use crate::node;
use crate::token;

pub fn parse(tokens: &Vec<token::Token>) -> node::Node {
    parse_expression(tokens, &mut 0).unwrap()
}

fn parse_expression(tokens: &Vec<token::Token>, i: &mut usize) -> Option<node::Node> {
    if *i < tokens.len() {
        let current: usize = *i;
        let n: node::Node = parse_bracketed_expression(tokens, i);
        if *i == current {
            panic!("Parsing error");
        }
        return Some(n);
    }
    None
}

fn parse_bracketed_expression(tokens: &Vec<token::Token>, i: &mut usize) -> node::Node {
    if tokens[*i] == token::Token::LeftBracket {
        *i += 1;
        let n: node::Node = parse_expression(tokens, i).unwrap();
        if tokens[*i] != token::Token::RightBracket {
            panic!("Expected right bracket");
        }
        node::Node::BracketNode(Box::new(n))
    } else {
        parse_expression(tokens, i).unwrap()
    }
}
